# Commands

All commands are under `/cellphones` (aliases `/phone`, `/phones`). This lists the commands of the first release as scoped in [MVP.md](MVP.md); it is updated as they land.

| Command | Permission | Description |
|---|---|---|
| `/cellphones help` | `cellphones.use` | List commands |
| `/cellphones info` | `cellphones.use` | Whether you have a phone, whether you have signal, and how many towers are in range |
| `/cellphones towers` | `cellphones.admin` | List every tower: id, world, coordinates, radius, owner |
| `/cellphones tower remove <id>` | `cellphones.admin` | Unregister a tower and turn its block into a plain block |
| `/cellphones give <phone\|tower> [player]` | `cellphones.admin` | Hand out an item |
| `/cellphones reload` | `cellphones.admin` | Reload `config.yml` |

## Permissions

| Permission | Default | Grants |
|---|---|---|
| `cellphones.use` | everyone | Using the phone network and `help`/`info` |
| `cellphones.tower.place` | everyone | Placing and breaking cell towers |
| `cellphones.admin` | op | Everything above plus `towers`, `tower remove`, `give`, `reload` |
