# Cellphones — Architecture

How the plugin described in [MVP.md](MVP.md) is built. This is the reference for anyone adding to it, human or agent; it says where things go and which rules are not negotiable.

## Stack

| | |
|---|---|
| Language | Java 21 (no Kotlin) |
| Build | Gradle 8.14, Kotlin DSL (`build.gradle.kts`); `./gradlew clean build` produces `build/libs/Cellphones-<version>.jar` |
| API | `org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT`, `compileOnly`; `api-version: 1.21` |
| Tests | JUnit 5 + Mockito, under `src/test/java` |
| Dependencies at runtime | none; nothing is shaded |
| CI | `.github/workflows/build.yml` (JDK 21) on push/PR to `main`; `release.yml` attaches the jar to a published release; `dev-release.yml` republishes the rolling `dev` pre-release |
| Verification | [release-gates](https://github.com/Dans-Plugins/release-gates) boot gate on Spigot 26.2; the save-compat gate applies because `towers.json` is persistent state |

The version lives only in `build.gradle.kts`; `plugin.yml` reads it through resource expansion.

## Package layout

```
dansplugins.cellphones
├── Cellphones            plugin entry point: wires everything below, nothing else
├── config/               ConfigService (owns the current PluginConfig + Messages, (re)loads them), PluginConfig (typed, immutable), Messages (formatting)
├── items/                ItemTags (namespaced keys), PhoneItem, TowerItem, RecipeRegistrar
├── towers/               Tower (record), TowerRegistry (in-memory, world-indexed), TowerStore (towers.json codec), Coverage (pure geometry)
├── network/              Network: given a registry, answers "who has signal" and "can A reach B"; SingleNetwork is the MVP implementation
├── chat/                 PhoneChatListener: the one place vanilla chat is observed
├── listeners/            TowerPlaceListener, TowerBreakListener (break, explode, piston)
└── commands/             CellphonesCommand and one class per subcommand
```

Rules:

- `towers.Coverage`, `towers.TowerStore`'s codec, and `network.*` have **no Bukkit imports** beyond `UUID`/plain values so they are unit-testable without a server. Bukkit objects enter through `Cellphones` and the listeners.
- The entry point only constructs and registers. No logic in `Cellphones.java`.
- Every user-facing string comes from `config.yml` → `Messages`. No hard-coded chat text.

## Items

Items are identified by a `PersistentDataContainer` tag under the plugin's namespace:

| Key | Type | Meaning |
|---|---|---|
| `cellphones:phone` | `BYTE` = 1 | this item is a phone |
| `cellphones:tower` | `BYTE` = 1 | this item is a tower |
| `cellphones:item-version` | `INTEGER` | layout version of the tag set, for future migrations |

Display name, lore, and `custom-model-data` are cosmetic and are set from config when the item is created. Nothing ever reads them back. `ItemTags.isPhone(ItemStack)` and `isTower` are the only way to ask.

Recipes are registered on enable when `craftable` is true and are keyed under the plugin namespace so `/cellphones reload` can replace them.

## Towers

A `Tower` is immutable: `id (UUID)`, `world (UUID)`, `x, y, z (int)`, `radius (int)`, `owner (UUID, nullable)`, `placedAt (epoch millis)`.

`TowerRegistry` holds towers in memory, indexed by world, and is the single source of truth while the server runs. Every mutation goes through it, and it notifies `TowerStore` to save.

### Coverage

`Coverage.covers(...)` is pure geometry (see the class). `TowerRegistry.towersInRange(world, x, y, z)` returns the towers covering a point; `Network.hasSignal(player)` is `hasPhone(player) && !towersInRange(...).isEmpty()`. With a few hundred towers a linear scan per chat message is fine; a spatial index is an optimisation for later, behind the same method.

### Block events

- `BlockPlaceEvent` with a tagged tower item in hand → register a tower at the placed block. The block itself is an ordinary block of the configured material; the registry is what makes it a tower.
- `BlockBreakEvent` on a registered tower → unregister, cancel the vanilla drop, drop a tower item.
- `BlockExplodeEvent` / `EntityExplodeEvent` containing a registered tower → unregister, no drop.
- `BlockPistonExtendEvent` / `BlockPistonRetractEvent` moving a registered tower → cancel.

Only the tower's block is a tower. A lightning rod placed by hand, or already in the world, is never a tower.

## Persistence

`plugins/Cellphones/towers.json`:

```json
{
  "version": 1,
  "towers": [
    {
      "id": "6f1c…",
      "world": "3a9e…",
      "x": 120, "y": 71, "z": -340,
      "radius": 200,
      "owner": "0a9f…",
      "placedAt": 1758326400000
    }
  ]
}
```

Rules, in order of importance:

1. **Write atomically.** Serialize to `towers.json.tmp`, then `Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)`. The file on disk is always either the previous complete state or the new complete state.
2. **Never drop what cannot be understood.** A tower whose world is not loaded is kept in the file and logged, not deleted. An unknown field is preserved on round-trip. A file with a `version` newer than the plugin understands is not loaded, not overwritten, and the plugin disables itself with a clear message rather than risk writing an older format over newer data.
3. **Migrate forward only, explicitly.** A bump of `version` comes with a migration in `TowerStore` and a fixture under `src/test/resources/fixtures/towers-v<N>.json` that the codec test round-trips.
4. **Save on every mutation** (debounced by one tick) and on disable. Loading happens once, on enable, before listeners are registered.
5. **Back up before migrating.** When a migration runs, the original file is copied to `towers.json.v<N>.bak` first.

The codec (`TowerStore`) is a pure `String ↔ List<Tower>` transform using the JSON library bundled with Spigot (Gson) and is unit-tested against fixtures.

## Chat pipeline

The whole "never touch vanilla chat" principle lives in one class, `chat/PhoneChatListener`:

```
AsyncPlayerChatEvent @ MONITOR, ignoreCancelled = true
  ├─ sender has no phone            → return
  ├─ sender has phone, no signal    → action bar: no-signal; return
  └─ sender has signal
       for each online player p ≠ sender:
         p in event.getRecipients()  → skip (they hear the vanilla message)
         !network.canReach(sender,p) → skip
         else                        → p.sendMessage(format(phone-format, sender, message))
```

- **`MONITOR` priority** so every other plugin — including the proximity-chat plugin — has finished deciding the recipient set before Cellphones reads it. The recipient set is only read, never modified.
- **`ignoreCancelled = true`**: a cancelled message is not a message.
- **Thread safety**: `AsyncPlayerChatEvent` is asynchronous. `TowerRegistry` reads are done through an immutable snapshot (`List.copyOf`) swapped on mutation, so the listener never touches a structure the main thread is writing. `Player#sendMessage` is safe off the main thread; nothing else Bukkit-side is called from the listener.
- Recipients on Paper: Paper's `AsyncChatEvent` is not used; the Spigot event is available on both and is the org's target.

`network.canReach(a, b)` is `SingleNetwork`'s `hasSignal(a) && hasSignal(b) && sameWorld(a, b)` in the MVP. `LinkedNetwork` (planned) replaces it with graph connectivity over towers within `link-range` of each other; the listener does not change.

## Commands

`CellphonesCommand` implements `TabExecutor`, parses the first argument, and dispatches to one small class per subcommand under `commands/`. Each subcommand declares its permission and checks it before doing anything. Tab completion is provided for subcommand names, `phone|tower`, tower ids, and player names.

## Configuration

`config/ConfigService.load()` runs on enable and on `/cellphones reload`: `saveDefaultConfig` writes the bundled file if absent, `reloadConfig` re-reads it, `options().copyDefaults(true)` + `saveConfig()` add any missing keys from the bundled defaults, and `config/PluginConfig.from(...)` turns the result into a typed, immutable object. Nothing else calls `getConfig()`, and every read uses the one-argument getters so a key an operator deleted still falls through to the bundled default (the two-argument getters ignore jar defaults). Keys are never removed. A `config-version` newer than `PluginConfig.CURRENT_VERSION` is logged and loaded as far as it is understood; when a version 2 layout exists, older files are migrated key by key with a log line per change.

Values are validated on load and replaced with a logged warning rather than disabling the plugin: an unknown, air, or legacy material falls back to the bundled default, a negative `tower.radius` clamps to 0, and an unsupported `network.mode` falls back to `single`. `Material#isItem`/`#isAir` consult the server registry, so the material check is by identity (the three air constants and `isLegacy()`) and stays unit-testable.

`config/Messages` formats `messages.*` — `&` colour codes and `{placeholder}` substitution — and renders a missing key as a visible `<missing message: key>` marker so documentation drift is noticed rather than hidden. Help descriptions are `messages.help.<subcommand>`.

## Testing

| Area | How |
|---|---|
| `Coverage` and other pure logic | JUnit, no mocks (`CoverageTest`) |
| `PluginConfig`, `Messages` | JUnit against the bundled `config.yml` and an operator override file loaded as `YamlConfiguration` (`PluginConfigTest`, `MessagesTest`) |
| `TowerStore` codec | JUnit against JSON fixtures, including an unknown-field and a newer-version fixture |
| `SingleNetwork` | JUnit with a hand-built `TowerRegistry` and fake player positions |
| `PhoneChatListener` | JUnit with Mockito: a mocked event, recipients set, and players; asserts which players get `sendMessage` |
| Commands | Mockito `CommandSender`/`Player` with the real `Messages` (`CellphonesCommandTest`) |
| Block listeners, recipes, real chat | The mineflayer scenario in MVP.md's acceptance criteria and the release-gates boot gate |

## Usage reporting

Like every Dans-Plugins plugin, Cellphones will vendor the one-file [trace](https://github.com/Stephenson-Software/trace) Java client and report startups, with the loud opt-out (`plugins/Cellphones/config.yml` → `usage-reporting: false`, the `TRACE_USAGE_REPORTING` env var, or `DO_NOT_TRACK`) the org standardised on. The write key is minted per program on the trace box and is never committed. This is wired as a follow-up issue, not in the scaffold.

## Decisions log

| Decision | Why |
|---|---|
| Read the recipient set instead of limiting chat ourselves | Keeps Cellphones composable with whatever proximity-chat plugin an operator already trusts, and keeps "never touch vanilla chat" true in one place |
| Tag-based item identity | Renaming, anvils, and resource packs cannot create or destroy a phone; the tag survives everything except item destruction |
| JSON file, not a database | Towers are few, change rarely, and operators can read and back up a file; keeps the save-compat gate simple |
| Single network first | The interesting design (linked networks) needs the tower plumbing to exist first; shipping it later behind `network.mode` costs nothing now |
| No phone toggle in the MVP | A phone that is always on is the simplest rule to explain in chat; a toggle can be added without changing the pipeline |
