# AI Response Diff Viewer

<!-- Plugin description -->
A lightweight IntelliJ IDEA plugin that converts AI-generated code suggestions into a structured, IDE-native review workflow — before any changes are applied to your codebase.
<!-- Plugin description end -->

---

## Project Overview

AI Response Diff Viewer adds a missing step to the modern AI-assisted development workflow: **review before apply**.

The plugin provides a dedicated ToolWindow where developers paste any AI-generated response. It extracts the code block, resolves the relevant file or selection from the active editor, and prepares a diff — opening IntelliJ's native diff viewer so the developer sees exactly what would change before deciding.

No code is applied automatically.

---

## Problem Statement

AI coding assistants are now a standard part of development. But the typical interaction has a structural gap:

```
Developer asks AI → AI returns code → Developer copies and pastes → Hopes nothing breaks
```

There is no review step. The developer either applies the suggestion blindly or manually compares the AI output against existing code — which is slow and error-prone.

AI-generated code can introduce subtle logic errors, conflict with existing patterns, or modify more than what was asked for. An IDE-native diff review addresses this directly. Developers already trust the diff interface — it is the same tool they use for Git and merge conflict resolution. Bringing AI suggestions into that interface makes review natural rather than manual.

---

## Why This Plugin

The internship position is on the **JetBrains AI Assistant Chat team**. The most relevant thing to demonstrate is not building another chat interface — JetBrains already has one. What matters more is thinking about the problems that exist *around* the chat interface.

One of the clearest unsolved problems in AI-assisted coding is the gap between receiving a suggestion and safely integrating it. This plugin addresses that gap directly, inside the IDE, using native platform components.

The plugin is intentionally **provider-agnostic** — it works with responses from JetBrains AI Assistant, Claude, ChatGPT, Copilot, or any other source. The review problem exists regardless of which AI generated the code.

---

## AI-Related Logic

The AI-related value here is not in generating text. It is in **parsing, contextualizing, and safely staging** AI-generated code for integration.

**AI Response Parsing**
The `MarkdownCodeBlockParser` extracts code blocks from raw AI responses using the markdown format all major AI tools produce. Language tags are detected when present; multiple blocks are supported — the MVP uses the first, but the architecture is ready for hunk-level selection.

**Context Resolution**
The `EditorContextResolver` reads the current IDE state: if the developer has selected a code region, that becomes the comparison target. If not, the full content of the active file is used. This is what makes the review meaningful — the AI suggestion is compared against the actual code it would replace, not a placeholder.

**DiffSession Assembly**
The resolved context and extracted code block are combined into a `DiffSession` — an immutable object carrying both sides of the diff, the file path, and the detected language. Everything the diff viewer needs is in one place.

**Review-First Design**
Nothing is applied automatically. The `FileApplyService` (Phase 6) will apply changes only on explicit confirmation, using IntelliJ's `WriteCommandAction` to preserve undo/redo support.

---

## Architecture

The plugin is structured around a central orchestrator coordinating isolated, single-responsibility services. No layer knows the implementation details of another.

```
ToolWindow          →  UI entry point, no business logic
DiffOrchestrator    →  pipeline coordinator
CodeBlockParser     →  extracts code blocks from AI response
ContextResolver     →  reads active editor selection or file
DiffSession         →  immutable carrier for the diff payload
DiffViewerManager   →  opens IntelliJ native diff UI
FileApplyService    →  applies accepted changes safely
ErrorHandler        →  centralizes all failure scenarios
```

All services are defined as interfaces. The `DiffOrchestrator` depends only on abstractions — implementations are injected, keeping each layer independently testable and replaceable.

Errors are modeled as `sealed class DiffViewerError`, which forces exhaustive handling at every `when` expression — no failure case can be silently missed.

---

## Current Progress

### Phase 1 — Skeleton 
- Gradle + IntelliJ Platform Plugin SDK configured
- ToolWindow registered in `plugin.xml` and visible in sandbox IDE
- Plugin compiles and runs via `./gradlew runIde`

### Phase 2 — Domain Models & Core Architecture 
- Domain models defined: `AiResponse`, `CodeBlock`, `DiffSession`, `TargetContext`
- Service interfaces established with clear single responsibilities
- `DiffOrchestrator` wired as the central pipeline coordinator
- `sealed class DiffViewerError` covers all failure scenarios

### Phase 3 — Input Layer & Code Parsing 
- ToolWindow UI: text area, Compare button, status label
- `MarkdownCodeBlockParser` implemented with regex-based extraction
- Handles: empty input, missing blocks, multiple blocks, missing language tag
- Unit tests cover all parsing scenarios

### Phase 4 — Context Resolution 
- `EditorContextResolver` reads active selection or falls back to full file
- `DiffSession` assembled and passed through the orchestrator pipeline
- ToolWindow fully delegates to orchestrator — zero business logic in UI layer

### Phase 5 — Diff Engine & Native Diff Viewer 
- `IntelliJDiffViewerManager` implemented using IntelliJ's native diff API
- `DiffContentFactory` creates typed content from both code sides
- `SimpleDiffRequest` opens side-by-side diff with clear labels
- Full pipeline now functional: paste → parse → resolve → diff

### In Progress
- **Phase 6** — Accept / Reject apply flow with undo/redo
- **Phase 7** — Error handling polish and edge case coverage

---

## How to Run

```bash
git clone https://github.com/antiiicm03/ai-response-diff-viewer.git
```

Open the project in IntelliJ IDEA, then:

```bash
./gradlew runIde
```

In the sandbox IDE: open any source file, find the **AI Diff Viewer** panel at the bottom, paste an AI response containing a code block, and click **Compare**.

---

## Technical Decisions

**Official JetBrains plugin template**
Provides a correctly configured Gradle setup, GitHub Actions CI, and plugin verification out of the box — reducing setup risk and following platform conventions from the start.

**Native IntelliJ diff viewer**
Building a custom diff UI would introduce visual inconsistency and significant implementation overhead. IntelliJ's diff component is mature, familiar to developers, and the right tool for this job.

**Provider-agnostic input**
The parsing layer operates on plain text. Coupling it to a specific AI provider would limit usefulness without adding value. The plugin works with any source, with no API keys or external connections required.

**Review-first over auto-apply**
The explicit review step is the core value of the plugin, not a limitation. It is also what makes the plugin safe to use on real codebases.

---

## Future Improvements

- **Direct JetBrains AI Assistant integration** — automatic detection without manual paste
- **Partial hunk acceptance** — accept or reject individual diff sections
- **Multi-file patch support** — handle suggestions that span multiple files
- **Smarter file inference** — match AI-suggested identifiers to the correct file automatically
- **Session history** — persist recent diff sessions for review and audit

---

## Notes for Evaluator

This project is scoped deliberately. The goal was to build something well-structured that solves a real problem clearly — not to maximize feature count.

The architecture is designed to be extended without rewriting. Adding a new parser, input provider, or viewer means implementing one interface and registering it — nothing else changes.

Phases 1–4 are complete. The foundation, domain model, parsing pipeline, and context resolution are all in place. The remaining phases build on this foundation to complete the full review workflow.