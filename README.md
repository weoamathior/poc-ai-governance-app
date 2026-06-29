# governance-audit — Audit Ledger Branch

This orphan branch is the durable, append-only audit ledger for the AI governance pipeline.
Each merged, evaluated PR has a record at `audit-log/{prNumber}-{mergeSha}.json` capturing
the final post-dismissal findings — including, for any dismissed finding, who dismissed it,
in what role, why, when, and against which standards ref.

It is written only by the `audit-log` workflow (on the default branch) and must not be
edited by hand. It lives on its own branch because the default branch is protected by the
required `governance/gate` check, which rejects direct pushes.
