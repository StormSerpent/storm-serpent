# hytale-wip-mod

## Development

### Cloning

Use `--recurse-submodules` when cloning the repository:
```shell
git clone --recurse-submodules git@github.com:TheNullicorn/hytale-wip-mod.git
```
This project uses git submodules to include the Serpentine plugin, found under `libs/serpentine/`.


### Running a local development server

The `runServer` gradle task is included for booting up a local development server set up with the mod and its
dependencies all loaded.

1. Copy `Assets.zip` for <u>the same Hytale version you're compiling with</u> to `run/Assets.zip`.
2. Run the `runServer` gradle task
3. Follow any Hytale login prompts if they appear


### Generating asset schemas

The `generateAssetSchema` gradle task is included for dumping JSON asset schemas from the server into
`src/main/resources`. You can then open that folder as a workspace in Visual Studio Code, where the schemas will now be
used to validate and provide suggestions while you type in JSON assets.
