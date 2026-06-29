# Audit Log

The governance audit records live on a dedicated **`governance-audit`** branch, not on the
default branch. They are written there by the `audit-log` workflow at merge time (it runs
on `pull_request[closed]` for merged PRs). The records are kept off the default branch on
purpose: that branch is protected by the required `governance/gate` check, which rejects
any direct push — and the audit ledger is conceptually separate from code anyway, an
append-only record on its own branch. To read the records, check out `governance-audit`.

This README is the only thing about the audit log that lives on the default branch. The
records themselves are not maintained by hand and must not be edited manually — treat them
as a forensic record produced by automation.

When a pull request that was evaluated by the pipeline is merged, the workflow writes a
JSON file named `{prNumber}-{mergeSha}.json`. The record is an object carrying the merge
metadata (`prNumber`, `mergeCommitSha`, `mergedAt`, `recordedAt`) and a `findings` array.
Those findings are read from the pull request's sticky governance comment, so they are the
**final, post-dismissal** state: each finding conforms to the finding schema in
`poc-ai-governance-standards/findings-schema/finding.schema.json`, carries its
`evaluatorVersion` (the pinned standards ref active at evaluation), and — for any finding
that was dismissed — carries the dismissal provenance (who dismissed it, in what role, why,
and when). The record therefore captures not just what the evaluator observed, but how each
observation was ultimately disposed and on whose authority.

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
