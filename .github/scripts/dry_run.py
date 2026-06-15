#!/usr/bin/env python3
"""
Local dry-run harness for the PR evaluator.

Stages the same inputs the CI workflow assembles in Step 4 (the prompt template,
the concatenated active standards, and a sample PR diff), runs evaluate.py exactly
as CI would, then prints the resulting findings.json plus token usage and an
estimated cost — so you can see real output and reconcile it against
evaluator/COST-MODEL.md before pushing anything to CI.

Two modes:

  --stub  (default)  No API key, no network. Injects a fake `anthropic` module
                     that returns a canned, schema-shaped response and estimates
                     tokens from text length (chars / 4). Exercises the whole
                     flow offline.
  --live             Requires ANTHROPIC_API_KEY in the environment and
                     `pip install anthropic`. Calls the real model and reports
                     the real token counts from the API.

Usage:
  python .github/scripts/dry_run.py                 # stub, Haiku pricing
  python .github/scripts/dry_run.py --model claude-sonnet-4-6
  python .github/scripts/dry_run.py --live          # real API call
"""

import argparse
import json
import os
import pathlib
import subprocess
import sys
import tempfile

# Per-1M token prices: (input, output). Mirrors evaluator/COST-MODEL.md.
PRICES = {
    "claude-haiku-4-5": (1.0, 5.0),
    "claude-sonnet-4-6": (3.0, 15.0),
    "claude-opus-4-8": (5.0, 25.0),
}

# A small, realistic PR diff that trips the demo's intentional conditions:
# a new service method with no AI-provenance comment (STD-001, required) and no
# accompanying test (STD-002, advisory).
SAMPLE_DIFF = """\
diff --git a/src/main/java/com/example/governance/service/OrderService.java b/src/main/java/com/example/governance/service/OrderService.java
index 1111111..2222222 100644
--- a/src/main/java/com/example/governance/service/OrderService.java
+++ b/src/main/java/com/example/governance/service/OrderService.java
@@ -18,4 +18,12 @@ public class OrderService {
     public List<Order> getOrdersForCustomer(String customerId) {
         return Collections.emptyList();
     }
+
+    public Order cancelOrder(Long id) {
+        Order order = getOrder(id);
+        if (order != null && !"SHIPPED".equals(order.getStatus())) {
+            order.setStatus("CANCELLED");
+        }
+        return order;
+    }
 }
"""

# Canned model output for stub mode: the detection-only slice the real model is
# constrained to emit (evaluate.py stamps the audit metadata afterward).
STUB_FINDINGS = {
    "findings": [
        {
            "standardId": "STD-001",
            "severity": "required",
            "description": "New cancelOrder service logic was added with no AI-provenance comment.",
            "file": "src/main/java/com/example/governance/service/OrderService.java",
            "lineRange": {"start": 21, "end": 28},
        },
        {
            "standardId": "STD-002",
            "severity": "advisory",
            "description": "A new service method was added with no corresponding test file in the PR.",
            "file": "src/main/java/com/example/governance/service/OrderService.java",
            "lineRange": {"start": 21, "end": 28},
        },
    ]
}

# A fake `anthropic` module, written into the staging dir for stub runs. It returns
# the canned findings and estimates tokens from the assembled prompt length.
FAKE_ANTHROPIC = '''\
import os

class _Block:
    def __init__(self, text):
        self.type = "text"
        self.text = text

class _Usage:
    def __init__(self, i, o):
        self.input_tokens = i
        self.output_tokens = o

class _Resp:
    def __init__(self, text, i, o):
        self.content = [_Block(text)]
        self.usage = _Usage(i, o)

class _Messages:
    def create(self, model, max_tokens, output_config, messages):
        # Canned, schema-shaped response passed in by the harness via the env.
        text = os.environ["DRY_RUN_STUB_FINDINGS"]
        prompt = messages[0]["content"]
        return _Resp(text, max(1, len(prompt) // 4), max(1, len(text) // 4))

class Anthropic:
    def __init__(self, *a, **k):
        self.messages = _Messages()
'''


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--live", action="store_true", help="call the real API")
    ap.add_argument("--model", default="claude-haiku-4-5")
    args = ap.parse_args()

    here = pathlib.Path(__file__).resolve()
    evaluate_py = here.parent / "evaluate.py"
    standards = here.parents[3] / "poc-ai-governance-standards"
    if not standards.is_dir():
        sys.exit(f"Could not find standards repo at {standards}")

    with tempfile.TemporaryDirectory() as d:
        work = pathlib.Path(d)
        # Stage the inputs exactly as workflow Step 4 does.
        (work / "prompt_template.md").write_text(
            (standards / "evaluator" / "pr-evaluation-prompt.md").read_text()
        )
        std_files = sorted((standards / "standards").glob("STD-*.md"))
        (work / "active_standards.md").write_text(
            "\n".join(f.read_text() for f in std_files)
        )
        (work / "pr.diff").write_text(SAMPLE_DIFF)

        env = dict(os.environ)
        env.update(
            EVALUATOR_MODEL=args.model,
            EVALUATOR_VERSION="dryrun-ref-abc123",
            PR_NUMBER="999",
            COMMIT_SHA="0000000dryrun",
        )

        if not args.live:
            (work / "anthropic.py").write_text(FAKE_ANTHROPIC)
            env["PYTHONPATH"] = str(work)  # stub takes precedence over any installed SDK
            env["DRY_RUN_STUB_FINDINGS"] = json.dumps(STUB_FINDINGS)
            env.setdefault("ANTHROPIC_API_KEY", "stub-not-used")
        elif not env.get("ANTHROPIC_API_KEY"):
            sys.exit("--live requires ANTHROPIC_API_KEY in the environment")

        proc = subprocess.run(
            [sys.executable, str(evaluate_py)],
            cwd=work, env=env, capture_output=True, text=True,
        )
        if proc.returncode != 0:
            sys.stderr.write(proc.stdout + proc.stderr)
            sys.exit(f"evaluate.py failed (exit {proc.returncode})")

        findings = json.loads((work / "findings.json").read_text())
        usage_line = proc.stderr.strip().splitlines()[-1] if proc.stderr.strip() else ""

    mode = "LIVE" if args.live else "STUB"
    print(f"\n=== Dry run ({mode}) — model: {args.model} ===\n")
    print(f"findings.json ({len(findings)} finding(s)):\n")
    print(json.dumps(findings, indent=2))
    print(f"\nevaluate.py usage log: {usage_line}")

    # Parse the token counts the script logged and estimate cost.
    toks = {k: int(v) for k, v in
            (p.split("=") for p in usage_line.split() if p.startswith(("input_tokens=", "output_tokens=")))}
    if toks and args.model in PRICES:
        pin, pout = PRICES[args.model]
        cost = toks["input_tokens"] / 1e6 * pin + toks["output_tokens"] / 1e6 * pout
        print(f"\nEstimated cost for THIS PR on {args.model}: ${cost:.5f}")
        print("(stub tokens are length-estimated; run --live for the real count)")
        print("Per-model cost for the same token counts:")
        for m, (i, o) in PRICES.items():
            c = toks["input_tokens"] / 1e6 * i + toks["output_tokens"] / 1e6 * o
            print(f"  {m:<20} ${c:.5f}")


if __name__ == "__main__":
    main()
