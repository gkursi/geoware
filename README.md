# Geoware
Fabric 1.21.8 ~~ghost~~ client for 1.8/1.9+ sword pvp + some misc stuff

## Usage
### Commands
- Prefix: `?`
- Config modules with `set`
- Toggle modules with `toggle`
### Configs
- Save/load configs with `config save/load name`
- Create new configs with `config export name scope` (scope being all/modules/visuals/...)
- You can find configs under `.minecraft/GeoWare/config/`
- Last used configs are stored in `.minecraft/GeoWare/configs.json`

## Building
The only required dependency is [multirender](https://github.com/gkursi/multirender).
You can either have it in the same directory as the root of this project,
or remove `includeBuild` from `settings.gradle.kts` and include it from jitpack.

## Contributing
All prs are welcome, follow the [contribution guidelines](./CONTRIBUTING.MD).

## Credit
- LiquidBounce - TrackPack patch, Teams module
