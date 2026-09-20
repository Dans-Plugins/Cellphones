# Cellphones

[![Build](https://github.com/Dans-Plugins/Cellphones/actions/workflows/build.yml/badge.svg)](https://github.com/Dans-Plugins/Cellphones/actions/workflows/build.yml)

## Description

Cell towers and phones: a chat network for servers whose chat is local, so players with a phone in their inventory can talk beyond earshot when they are in tower coverage.

The plugin is in early development. There is no release yet.

## Requirements

- Spigot or Paper 1.21 or newer
- Java 21 (the plugin is written in Kotlin; the Kotlin standard library is bundled in the jar)

## Building

Linux / macOS:

    ./gradlew build

Windows:

    .\gradlew.bat build

The plugin jar is written to `build/libs/`. Copy it into the `plugins` folder of your server and restart.

## Testing

    ./gradlew clean test

`BUILD SUCCESSFUL` means the tests passed. CI runs `./gradlew clean build` on every pull request, which includes the tests.

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)

## Authors and Contributors

- Daniel Stephenson (DanTheTechMan)

## License

Cellphones is released under the [GNU General Public License v3.0](LICENSE).
