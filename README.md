# Threat Level Mod (Forge 1.8.9)

**Threat Level** is a lightweight PvP utility mod for Minecraft 1.8.9 that helps you instantly assess the strength of your opponents. It dynamically calculates a "threat score" for other players based on their visible equipment and status, displaying a color-coded indicator next to their name.

![Threat Level Example](https://i.imgur.com/placeholderexample.png)
*(Note: Example image placeholder)*

## Features

- **Dynamic Threat Calculation**: Automatically evaluates other players based on:
  - **Health** (35% weight): Current health + absorption hearts.
  - **Armor** (35% weight): Protection value of equipped armor.
  - **Enchantments** (30% weight): Armor protection levels and weapon damage/sharpness levels.
- **Intuitive Visual Indicators**: prepends a colored skull (☠) to player nametags:
  - **§aGreen ☠** (Low Threat): Target is significantly weaker than you.
  - **§eYellow ☠** (Equal Threat): Target is roughly on par with your gear and health.
  - **§cRed ☠** (High Threat): Target is stronger or better equipped than you.
- **Client-Side Safe**: Uses only player-facing data (visible armor, held items, synced health) to ensure fair play and avoid cheat detection systems.
- **Mod Compatibility**: Uses Forge's `NameFormat` event to ensure compatibility with other overlay mods, such as Hypixel health indicators.

## Installation

1. Ensure you have **Minecraft Forge** installed for version 1.8.9.
2. Download the latest `threatlvl-1.0.jar` from the releases page (or build from source).
3. Place the `.jar` file into your Minecraft `mods` folder:
    - Windows: `%appdata%/.minecraft/mods`
    - Linux: `~/.minecraft/mods`
    - macOS: `~/Library/Application Support/minecraft/mods`
4. Launch Minecraft.

## Usage

The mod works automatically upon installation. When you see another player, their nametag will update to reflect their relative threat level compared to your own.

- **Threat Formula**: `(HealthScore * 0.35) + (ArmorScore * 0.35) + (EnchantScore * 0.30)`
- The score is relative; a player in full iron is a "High Threat" if you are naked, but a "Low Threat" if you are in full diamond.

## Building from Source

To build this mod yourself, you will need **JDK 8**.

```bash
# Clone the repository
git clone https://github.com/yourusername/threat-lvl.git
cd threat-lvl

# Build with Gradle (Linux/macOS)
# Ensure JAVA_HOME points to JDK 8
./gradlew build

# Build with Gradle (Windows)
gradlew build
```

The compiled artifact will be located in `build/libs/`.

## License

This project is open source. Feel free to modify and distribute.
