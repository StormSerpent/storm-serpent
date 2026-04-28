# Storm Serpent

## Development

### Running a local development server

The `runServer` gradle task is included for booting up a local development server set up with the mod and its
dependencies all loaded.

1. Copy `Assets.zip` for *the same Hytale version you're compiling with* to `run/Assets.zip`.
2. Run the `runServer` gradle task
3. Follow any Hytale login prompts if they appear

### Generating asset schemas

The `generateAssetSchema` gradle task is included for dumping JSON asset schemas from the server into
`src/main/resources`. You can then open that folder as a workspace in Visual Studio Code, where the schemas will now be
used to validate and provide suggestions while you type in JSON assets.

## License

This repository uses multiple licenses. See NOTICE for the authoritative breakdown.

- Code: MIT (see [LICENSE.md](LICENSE.md))
- Serpentine submodule: MIT (see [TheNullicorn/serpentine/LICENSE.md](https://github.com/TheNullicorn/serpentine/blob/main/LICENSE.md))
- Sound effects (SFX & Ambience): CC BY 4.0 (see [`src/main/resources/Common/Sounds/LICENSE`](src/main/resources/Common/Sounds/LICENSE))
- Storm Serpent UI assets (map markers + boss healthbar): CC BY 4.0 (see [`src/main/resources/Common/UI/LICENSE`](src/main/resources/Common/UI/LICENSE))
- Storm Serpent models & textures: CC BY 4.0 (see [`src/main/resources/Common/NPC/Boss/Serpent_Storm/Models/LICENSE`](src/main/resources/Common/NPC/Boss/Serpent_Storm/Models/LICENSE))
- Music: All Rights Reserved (not stored in this repo)
