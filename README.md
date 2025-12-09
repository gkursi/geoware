# Geoware
Fabric 1.21.8 ~~ghost~~ client for 1.8/1.9+ sword pvp + some misc stuff

## Usage
### Commands
- Prefix: `::`
- Config modules with `set`
- Toggle modules with `toggle`
### Configs
- Save/load configs with `config save/load name`
- Create new configs with `config export name scope` (scope being friends/modules/visuals/...)
- You can find configs under `.minecraft/GeoWare/config`

## Building
The only required dependency is [multirender](https://github.com/gkursi/multirender).
You can either have it in the same directory as the root of this project,
or remove `includeBuild` from `settings.gradle.kts` and include it from jitpack.

## Credit
- LiquidBounce - TrackPack patch, Teams module
- Meteor - system structure inspiration (no code was taken)