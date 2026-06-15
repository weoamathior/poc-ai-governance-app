# Audit Log

This directory is written to by the `pr-evaluation` pipeline at merge time. It is not
maintained by hand, and it must not be edited manually. Treat everything in this directory
as a forensic record produced by automation.

When a pull request that was evaluated by the pipeline is merged, the pipeline writes a
JSON file named `{prNumber}-{commitSha}.json`. For an evaluated pull request, that file
contains an array of findings, each conforming to the finding schema defined in
`poc-ai-governance-standards/findings-schema/finding.schema.json`. The findings capture
what the evaluator observed, the severity of each observation, and how each was ultimately
disposed.

Pull requests that were auto-approved are recorded too, but more briefly. For an
auto-approved merge, the pipeline writes a minimal record noting which auto-approve rule
matched and that no LLM evaluation was performed. This keeps the audit trail complete:
every evaluated or auto-approved merge leaves a durable artifact here, so the history of
the pipeline's decisions can be reconstructed after the fact.

Because each file is keyed by pull request number and commit SHA, and because the findings
it contains record the git ref of the standards repository that was active at evaluation
time, the exact governance context of any past merge can be reconstructed from this
directory alone. That is the reason it exists, and the reason it must remain untouched by
manual edits.
