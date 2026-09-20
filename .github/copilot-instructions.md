# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

Read `MVP.md` (what the first release does and does not do) and `ARCHITECTURE.md`
(where code goes and which rules are fixed) before changing anything. If a change
needs to break a rule in `ARCHITECTURE.md`, change the document in the same pull request.

## Technology Stack

- Language: Java 21
- Build tool: Gradle (Kotlin DSL, `build.gradle.kts`); the version is set there only
- Target platform: Spigot / Paper 1.21+ (`spigot-api` 1.21.11, `compileOnly`)
- Test framework: JUnit 5 with Mockito

## Project Structure

- `src/main/java/dansplugins/cellphones/` – plugin source, packaged as laid out in `ARCHITECTURE.md`
  (`config`, `items`, `towers`, `network`, `chat`, `listeners`, `commands`)
- `src/main/resources/` – `plugin.yml` and `config.yml`
- `src/test/java/` – unit tests, mirroring the main tree; JSON fixtures under `src/test/resources/fixtures/`

## Coding Conventions

- Never cancel, rewrite, or change the recipients of a chat event. Cellphones only reads the
  final recipient set at `MONITOR` priority and sends extra copies.
- Identify phones and towers by their `PersistentDataContainer` tag, never by display name or lore.
- Every user-facing string comes from `config.yml` through the `Messages` class; never hard-code text.
- Keep `towers.Coverage`, the `TowerStore` codec, and `network.*` free of Bukkit imports so they
  stay unit-testable.
- `towers.json` is written atomically and entries are never dropped because they cannot be
  understood; a newer `version` than the plugin knows stops the plugin instead of being overwritten.
- No logic in `Cellphones.java`; it only constructs and registers.

## Contribution Workflow

- Branch from `main` for all changes.
- Open a pull request against `main`; the `Build` workflow must pass.
- Reference the related GitHub issue in every pull request description.
