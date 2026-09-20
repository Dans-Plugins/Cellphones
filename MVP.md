# Cellphones — MVP

This document fixes the scope of the first release. Everything under **In scope** ships in 1.0.0; everything under **Out of scope** does not, however tempting. [ARCHITECTURE.md](ARCHITECTURE.md) says how it is built.

## Problem

On a server with proximity chat, players cannot coordinate across the map. Cellphones gives them a way to do so that has a cost (a phone in the inventory), a footprint (towers that can be found, built, and destroyed), and a limit (coverage).

## Principles

1. **Never touch vanilla chat.** Cellphones does not cancel, rewrite, or change the recipients of a chat message. It only sends additional, phone-formatted copies to players who would not otherwise have heard it. Whatever proximity-chat plugin the operator runs stays in charge.
2. **Items are identified by tag, never by name.** A phone is a phone because of a persistent data tag, not because it is called "Phone". Renaming an item cannot create or destroy a phone.
3. **Save integrity first.** The tower file is never truncated, never rewritten in place, and never has entries dropped because a world is missing. See [ARCHITECTURE.md](ARCHITECTURE.md#persistence).

## User stories

- As a player, I can craft a phone and carry it anywhere in my inventory, and while I am inside a tower's coverage everyone else in coverage with a phone hears what I say.
- As a player, I can craft a cell tower, place it, and give the area around it coverage; breaking it takes the coverage away.
- As a player, I can ask whether I have signal.
- As a player out of coverage, I am told there is no signal when I speak, so I know the message only reached people nearby.
- As an operator, I can hand out phones and towers, list every tower, remove one, and reload the configuration without a restart.
- As an operator, I can turn crafting off so towers and phones are scarce and only come from me.

## In scope

### Items

| Item | Default material | Tag | How obtained |
|---|---|---|---|
| Phone | `RECOVERY_COMPASS` | `cellphones:phone` | Shaped recipe (configurable, can be disabled) or `/cellphones give phone` |
| Cell tower | `LIGHTNING_ROD` | `cellphones:tower` | Shaped recipe (configurable, can be disabled) or `/cellphones give tower` |

Both items have a display name and lore set on creation for recognisability, but only the tag matters. Materials are configurable; `custom-model-data` is exposed for resource packs.

### Towers

- Placing a tower item registers a tower at that block with the configured radius. The player is told the radius.
- Breaking the block unregisters the tower and drops the tower item (not a plain lightning rod).
- A tower destroyed by an explosion is unregistered and drops nothing.
- Tower blocks cannot be moved by pistons.
- Coverage is a sphere (or a cylinder when `tower.ignore-y` is true) of `radius` blocks around the tower, in the tower's world only.

### Phones and the network

- A player **has a phone** when any slot of their inventory (including off-hand) holds a tagged phone item.
- A player **has signal** when they have a phone and are inside the coverage of at least one tower in their world.
- `network.mode: single`: all towers form one network. Any two players with signal can talk, regardless of distance or which tower covers each of them.
- When a player with signal sends a chat message, every other online player with signal who is **not** among the message's final recipients receives it formatted with `messages.phone-format`. Players who were recipients hear it once, the normal way.
- A cancelled chat message is not relayed.
- When a player with a phone but no signal sends a chat message, they receive `messages.no-signal` on the action bar. The message itself is untouched.
- A player without a phone is unaffected in every way.

### Commands

| Command | Permission | Description |
|---|---|---|
| `/cellphones help` | `cellphones.use` | List commands |
| `/cellphones info` | `cellphones.use` | Whether you have a phone, whether you have signal, and how many towers are in range |
| `/cellphones towers` | `cellphones.admin` | List every tower: world, coordinates, radius, owner |
| `/cellphones tower remove <id>` | `cellphones.admin` | Unregister a tower and turn its block into a plain block |
| `/cellphones give <phone\|tower> [player]` | `cellphones.admin` | Hand out an item |
| `/cellphones reload` | `cellphones.admin` | Reload `config.yml` |

Aliases: `/phone`, `/phones`. Every command answers `help` for the boot gate.

### Configuration

The keys in [`config.yml`](src/main/resources/config.yml): tower material, radius, `ignore-y`, craftability; phone material and craftability; `network.mode`; every user-facing message. A `config-version` key lets later releases add keys without removing an operator's.

### Persistence

`plugins/Cellphones/towers.json` — the only state. Format and rules in [ARCHITECTURE.md](ARCHITECTURE.md#persistence).

## Out of scope (for the MVP)

- **Linked networks** (towers only relay to towers within a link range, so far-apart regions are separate networks). Planned as `network.mode: linked` in a later release; the config key exists so the default can be kept.
- Phone numbers, private calls, contacts, texting UI, or a `/call` command.
- Any prefix or toggle to choose between "speak locally" and "speak on the phone". In the MVP, having signal means you are always on the phone.
- Battery, charging, signal strength, or range-based garbling.
- Cross-world coverage.
- Tower height bonuses or multi-block towers.
- Limiting vanilla chat range. Cellphones assumes another plugin does it.
- Integration with Medieval Factions (tower ownership by faction, claims) — a natural expansion, but a separate one.

## Acceptance criteria

Written so a mineflayer bot scenario and the [release-gates](https://github.com/Dans-Plugins/release-gates) boot gate can check them.

1. **Boot gate** passes: the plugin enables on a fresh Spigot 26.2 server, answers `help` for `cellphones`, stops cleanly, and enables again over its data folder.
2. Two bots A and B stand 500 blocks apart with no towers. A speaks; B does not receive a phone-formatted copy.
3. A tower is placed within 200 blocks of A and another within 200 blocks of B (`network.mode: single`). Both hold phones. A speaks; B receives exactly one message matching `phone-format`.
4. B drops their phone. A speaks; B receives nothing.
5. B picks the phone up and moves 250 blocks from the nearest tower. A speaks; B receives nothing. B speaks; B sees `no-signal` on the action bar.
6. The tower near A is broken. A speaks; B receives nothing. `/cellphones towers` lists one tower.
7. The server is restarted. `/cellphones towers` still lists that one tower with the same id, coordinates, and radius.
8. A stands 10 blocks from B, both with signal. A speaks; B receives the message exactly once (the vanilla copy, no phone copy).
9. `./gradlew clean test` passes; `Coverage` and the tower file codec are unit-tested.

## Release plan

- `0.1.0` — items, towers, single network, commands, persistence. Everything above.
- `0.2.0` — `network.mode: linked` with `network.link-range`; `/cellphones info` reports the reachable network size.
- Later — height bonus, signal strength, MF ownership integration, phone numbers and calls, each as its own RFC or issue.
