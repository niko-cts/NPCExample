# NPC Management Test Project

This is a test project demonstrating how to create and manage NPCs in Minecraft version 1.20.6 using Spigot. This project covers the following features:
1. Auto-respawn NPCs after logout or chunk reload.
2. Implementing pathfinding AI for NPCs without external libraries.
3. Enabling knockback & falldamage for NPCs.

For a detailed explanation about the code read this forum post: 

## Features

### Auto-respawn NPCs
- Ensures NPCs are automatically respawned even after player logout or chunk reload.
- Utilizes server notifications to determine when NPCs should be visible to players.
- Efficient handling to respect server viewing distance without performance loss.

### Pathfinding AI
- Implements custom pathfinding for NPCs.
- Spawns an invisible `PathfindingMob` and syncs its movements with the NPC.
- Ensures NPCs follow desired paths without external libraries.

### Knockback & Falldamage
- Enables knockback functionality for NPCs.
- Updated methods to be compatible with Minecraft version 1.20.6.

## Getting Started

### Prerequisites
- Minecraft version 1.20.6
- Spigot 1.20.6 (classifier: remapped-mojang)
- Java Development Kit (JDK) 21 or later

## Usage
- Copy the generated plugin .jar file to your Spigot server's plugins directory.
- Start your Spigot server.
- Use the provided commands to create and manage NPCs in-game:
  `/npctest spawn/here/teleport`

## License
This project is licensed under the Apache License. See the LICENSE file for details.

## Acknowledgements
- Special thanks to the [forum post](https://www.spigotmc.org/threads/nms-serverplayer-entityplayer-for-the-1-17-1-18-mojang-mappings-with-fall-damage-and-knockback.551281/) for the initial knockback implementation.
- This project uses the remapped-mojang version for Minecraft 1.20.6.
