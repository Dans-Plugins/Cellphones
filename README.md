# Cellphones

[![Build](https://github.com/Dans-Plugins/Cellphones/actions/workflows/build.yml/badge.svg)](https://github.com/Dans-Plugins/Cellphones/actions/workflows/build.yml)

## Description

Cellphones is a Minecraft plugin that adds cell towers and phones. It is made for servers whose chat is local: players normally only hear people near them, and a phone lets them talk to anyone else who has a phone, as long as both of them are inside the coverage of a cell tower.

Cellphones never changes vanilla chat. It does not limit chat range and it does not alter who hears an ordinary message; a proximity-chat plugin of the operator's choice does that. What Cellphones adds is an extra delivery: when a player in coverage speaks, every other player who has a phone and is in coverage — and who would not otherwise have heard the message — receives it over the phone network.

The plugin is at the planning stage. [MVP.md](MVP.md) describes the first release and [ARCHITECTURE.md](ARCHITECTURE.md) describes how it is built. This README documents the **experimental** channel until a stable release exists.

## Installation

### First Time Installation

There is no stable release yet. The experimental build is published from `main` on every change:

1. With [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager): `/dpm get cellphones --experimental`, or download the `dev` pre-release from the [releases page](https://github.com/Dans-Plugins/Cellphones/releases).
2. Place the jar in the `plugins` folder of your server.
3. Restart your server.

Cellphones requires Spigot or Paper 1.21 or newer and Java 21. It has no plugin dependencies.

### Companion Plugins

Cellphones is only useful next to a plugin that limits vanilla chat to a local range. Any proximity-chat plugin will do; Cellphones reads the final recipient list of each chat message and delivers to phone holders who were not on it.

## Usage

### Documentation

- [MVP](MVP.md) – What the first release does and does not do
- [Architecture](ARCHITECTURE.md) – How the plugin is built
- [Commands Reference](COMMANDS.md) – Complete list of all commands (written with the first release)
- [Configuration Guide](CONFIG.md) – Detailed configuration options (written with the first release)

### Quick Tour

- Craft or receive a **cell tower** and place it. Every player within its radius has signal.
- Craft or receive a **phone** and keep it anywhere in your inventory.
- Talk normally. While you have signal, every other phone holder with signal hears you, however far away they are.
- `/cellphones info` tells you whether you have signal and how many towers are in range.

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Cellphones/issues/new).

- [Known Bugs](https://github.com/Dans-Plugins/Cellphones/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)

## Testing

### Unit Tests

Linux:

    ./gradlew clean test

Windows:

    .\gradlew.bat clean test

If you see `BUILD SUCCESSFUL`, the tests have passed. The build needs JDK 21.

## Development

### Building

    ./gradlew build

The plugin jar is written to `build/libs/`.

### Test Server

A Docker-based test server with plugin hot-reloading, following the [DPC convention](https://github.com/Dans-Plugins/dpc-conventions/blob/main/docs/TESTING_AND_CI.md), is tracked as a follow-up. Until it lands, drop the jar into any Spigot 1.21+ server's `plugins` folder.

## Authors and Contributors

- Daniel Stephenson (DanTheTechMan)

## License

Cellphones is released under the [GNU General Public License v3.0](LICENSE).
