# Evaluator Regression Harness

This directory is the regression safety net for the LLM evaluator. As people tune the
prompt, edit standards, or build new ones, this corpus is what tells them whether a change
improved behavior or quietly broke something else.

It tests one thing: **does the evaluator flag what it should and stay silent on what it
shouldn't.** It does *not* test pipeline plumbing (auto-approve matching, the gate, the
sticky comment) — those are deterministic and belong in ordinary unit tests.

## Fixtures

Each file in `fixtures/` is one labeled case: a diff plus the ground truth of how the
evaluator should react. Fixtures are categorized by intent:

- **should-flag** (recall) — a genuine violation. `expect` lists the standards that must
  fire, with an optional severity.
- **should-not-flag** (precision) — a case that looks like a violation but isn't, drawn
  from each standard's *"what does not constitute a violation"* section. `expectNot` lists
  standards that must stay silent.

Because the model is non-deterministic, scoring is by tolerance, not equality. A fixture
runs `runs` times; an `expect` passes if the standard fired in at least `passThreshold`
runs, and an `expectNot` passes if it fired in at most `tolerance` runs.

## Running it

```bash
# Stub — deterministic harness self-test, no API key, no cost. Each fixture returns its
# own canned output, so everything passes by construction. Use it to see the report shape
# and to verify the scoring/report code.
python .github/scripts/run_evals.py

# Live — the real regression test. Calls the model for each run.
ANTHROPIC_API_KEY=sk-... python .github/scripts/run_evals.py --live --model claude-haiku-4-5

# Capture the current LIVE behavior as the baseline (do this once, deliberately).
python .github/scripts/run_evals.py --live --update-baseline
```

The runner prints a per-standard recall / false-positive table, a per-fixture pass/fail
breakdown, and a comparison against `baseline.json`. It exits non-zero if any fixture
fails or any metric regresses — so CI can gate on it.

## Before/after — how a tuning change is judged

`baseline.json` holds the last *accepted* behavior. A prompt or standards change is run
against it; the report shows the delta per standard. If recall drops or the false-positive
rate rises past tolerance, the run fails — that's the regression caught. When a change is
intentionally accepted (you *meant* to raise recall, and the new numbers are the new
normal), refresh the baseline with `--update-baseline` and commit it in the same change.

> The seed `baseline.json` holds idealized values. Replace it with a real `--live` capture
> before treating it as a gate — live numbers are lower than the stub ideal, and the live
> numbers are the honest bar.

## The flywheel

The corpus is only as good as its coverage. The discipline that keeps it useful: **every
false positive or false negative seen in a real PR becomes a new fixture.** When the
evaluator wrongly flags a config-only change, capture that diff, label it `should-not-flag`,
and commit it. From then on, no future prompt change can silently reintroduce that mistake.
Over time the corpus becomes the institutional memory of every error the evaluator has made.

## Pinning

The model is pinned per run (`--model`, default `claude-haiku-4-5`). A model upgrade is a
deliberate event: re-run the corpus on the new model, review the deltas, and refresh the
baseline — never let the model change as an invisible variable.
