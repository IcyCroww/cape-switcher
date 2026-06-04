# Cape Switcher

Cape Switcher is a client-side Fabric mod for Minecraft 1.21.4 that allows you to choose a local custom cape from a folder.

## Features

- Choose custom cape textures from local PNG files
- Three cape modes: Account (vanilla), Local (custom), None (hidden)
- In-game GUI wardrobe for browsing and selecting capes
- Hotkey (default: C) to open the cape wardrobe
- Auto-creates config and capes folders
- Error-safe: won't crash on missing/broken files
- English and Russian localization

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.4
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Place `cape-switcher-1.0.0.jar` into your `.minecraft/mods/` folder
4. Launch the game

## How to Add Capes

1. Navigate to `.minecraft/config/cape-switcher/capes/`
2. Place your PNG cape texture files there (e.g., `my_cape.png`)
3. Press **C** in-game (or your configured key) to open the Cape Wardrobe
4. Click on a cape to select it

Cape textures should follow the standard Minecraft cape format (64x32 or 128x64 pixels).

## Cape Modes

- **Account Cape** — Shows your official Minecraft account cape (if you have one)
- **Local Cape** — Shows a custom PNG cape from your capes folder
- **None** — Hides the cape entirely

## Important Notes

- **This mod is client-side only.** Other players will not see your selected local cape unless they use a compatible sync system.
- **Cape Switcher does not include or redistribute official Minecraft/Mojang cape textures.** Official account capes are shown only through the normal Minecraft account system using the "Account Cape" mode.
- To use custom capes, put your own PNG files into `.minecraft/config/cape-switcher/capes/`

## Building from Source

```
./gradlew build
```

The built jar will be in `build/libs/`.

## License

MIT
