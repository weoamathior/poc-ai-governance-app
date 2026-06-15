# poc-ai-governance-app

This is a minimal Spring Boot demo application. It is not a production service. It exists
to produce realistic pull requests that exercise the AI-assisted PR evaluation pipeline
defined in the `poc-ai-governance-standards` repository. The application code is
deliberately small and, in a couple of places, deliberately imperfect, because its purpose
is to generate pull requests that travel through the pipeline and demonstrate each of its
behaviors.

The repository is designed to demonstrate three pull request scenarios.

The first is a Wiremock-only pull request. When a change touches nothing but the Wiremock
mapping files under `src/test/resources/wiremock`, it matches auto-approve RULE-001 in the
standards repository. The pipeline short-circuits, approves the pull request, and performs
no LLM evaluation. This scenario shows the pipeline avoiding cost and noise on changes that
carry no production risk.

The second is a feature pull request containing AI-generated code. The `OrderController`
in this repository carries an AI provenance comment, while the `OrderService` it delegates
to does not. A pull request that introduces the kind of service logic found here triggers
the evaluator and produces a finding under STD-001, because it contains substantial service
logic with no provenance comment. This scenario shows the evaluator producing findings.

The third is a dependency pull request. When a change to `pom.xml` introduces a dependency
under a new `groupId`, it triggers STD-003, whose severity is *escalate*. This scenario
shows the pipeline routing a change to humans rather than resolving it automatically.

The `audit-log` directory is written to by the pipeline at merge time. It is the forensic
record of evaluated merges and must not be edited by hand. See `audit-log/README.md` for
details on what the pipeline writes there.

## Changelog

- Clarified that the audit-log directory is written only by the pipeline.
