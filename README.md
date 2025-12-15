# XM Lab

A KMP application designed to experiment with and replace companion app features for gadgets with a focus on headphones. Like the Sony Headphones app.

## Development Environment 

###  Nix (Recommended)

This project uses **Nix** to manage all development dependencies, ensuring a reproducible environment across machines (NixOS or Linux, WSL, MacOS (Nix darwin)).

#### Setup
To enter the development shell with all required tools (JDK, android-sdk, android-studio, gradle):

```bash
nix develop
```

#### Formatting

You can format the entire codebase (Kotlin and Nix files) using the configured formatter:

```bash
nix fmt
```

or if you are already inside the shell:

```Bash
treefmt
```
