# Configuration

`plugins/Cellphones/config.yml` is created on first start. Missing keys are added on every start; keys are never removed. This documents the first release's keys as scoped in [MVP.md](MVP.md).

| Key | Default | Meaning |
|---|---|---|
| `config-version` | `1` | Layout version of the file. Do not edit |
| `tower.material` | `LIGHTNING_ROD` | Material of the tower item. Tagged, so ordinary blocks of this material are not towers |
| `tower.radius` | `200` | Coverage radius in blocks around a tower |
| `tower.ignore-y` | `true` | Ignore height when measuring distance to a tower |
| `tower.craftable` | `true` | Whether players can craft towers. When `false`, only `/cellphones give tower` hands them out |
| `phone.material` | `RECOVERY_COMPASS` | Material of the phone item. Tagged, so ordinary items of this material are not phones |
| `phone.craftable` | `true` | Whether players can craft phones |
| `network.mode` | `single` | `single`: all towers form one network. `linked` is planned |
| `messages.*` | see file | Every user-facing message. Colour codes use `&`. Placeholders are listed next to each message |

Reload with `/cellphones reload`.
