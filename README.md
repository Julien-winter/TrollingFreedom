# TrollingFreedom

A badly coded troll plugin that surprisingly worked - originally written for
Minecraft 1.18 (see the [Spigot resource](https://www.spigotmc.org/resources/%E2%AD%90-trollingfreedom-%E2%AD%90-1-free-troll-plugin-with-gui.53419/)).

This fork is maintained for **Minecraft 1.21.x** (build target: Spigot API 1.21.11,
Java 21).

## What this fork changes

### Port to modern Minecraft (1.21.x)
- Rebuilt on **Spigot API 1.21.11**, Java 21, Maven (ProtocolLib 5.x, EssentialsX 2.21 provided).
- Updated every API that was removed or renamed since 1.18:
  - `Particle` renames (`SMOKE_NORMAL` → `SMOKE`, `EXPLOSION_NORMAL` → `EXPLOSION`, …),
    `PotionEffectType.SLOW` → `SLOWNESS`, `EntityType.FIREWORK` → `FIREWORK_ROCKET`,
    `EntityType.PRIMED_TNT` → `TNT`.
- Skull/heads are now built with the `PlayerProfile`/`PlayerTextures` API instead of the
  removed `Bukkit.getUnsafe().modifyItemStack(...)` NBT hack.
- The credits/demo/world-loading trolls write the `GAME_STATE_CHANGE` packet fields
  directly instead of reflecting into obfuscated NMS fields, so they survive mapping
  changes.
- The **Herobrine** troll no longer depends on the unmaintained Citizens repository -
  it spawns a real invisible `ArmorStand` with a Herobrine head.

### Bug fixes
- **ProtocolLib auto-install fixed**: the old Jenkins URL answered HTTP 403, which also
  left an empty/broken `ProtocolLib.jar` behind. The download now resolves through the
  public GitHub releases API, downloads to a temp file and only installs a file that
  actually looks like a JAR.
- **Graceful without ProtocolLib**: the Demo, Credits and WorldLoading trolls are only
  registered when ProtocolLib is loaded (previously the server logged
  `Failed to register events ... PacketContainer does not exist`).
- **Nick troll fixed**: no more `NullPointerException`/`ClassCastException` when
  Essentials is missing or broken, and the repeating nickname task is now actually
  cancelled on `/UnTroll` and on quit - previously it ran forever (and was unreachable
  because every call created a new instance).
- **InventoryRave fixed**: no legacy materials, per-player task cancellation instead of
  cancelling all scheduler tasks of the plugin, proper cleanup on player quit.
- Config update-checker is off by default (`values.update-checker: false`) so the fork
  does not nag about the original upstream version; `version` in `plugin.yml` now comes
  from Maven and the plugin's own version display is null-safe.

## Building

```bash
mvn -B clean package
```

Requires JDK 21+. The result is `target/trollingfreedom-<version>.jar`, drop it into
`plugins/` - ProtocolLib is downloaded automatically on first start if it is missing.

## Install / runtime notes

- **ProtocolLib** is required (auto-installed) - Demo, Credits and WorldLoading need it.
- **EssentialsX** is optional but required for the Nick troll.
- Tested on **Paper 1.21.11** (server starts, plugin enables, no registration errors).
