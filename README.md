# BridgingPractiseNK

[Chinese](README.zh.md) | English

Bridging practice plugin for Nukkit. Provides a dedicated practice world, block-based checkpoints, and optional leveling/scoreboard.

## Requirements

- Nukkit
- Dependency: MemoriesOfTime-GameCore

## Install

1. Place the plugin jar in `plugins/`.
2. Install `MemoriesOfTime-GameCore`.
3. Start the server once to generate `plugins/BridgingPractise/config.json` and language files.
4. Edit `plugins/BridgingPractise/config.json` to fit your world and rules.

## Commands

- `/bpractise join` - Join the practice area.
- `/bpractise leave` - Leave the practice area.

Command name is configurable via `practice.command`.

## Features

- Auto-copy and restore the practice world on shutdown.
- Practice blocks: respawn, stop, back-to-spawn, speedup, elevator.
- Optional level/experience system with scoreboard.
- Popup prompts for speed/distance stats.
- PvP protection, mob spawn blocking, and command whitelist.

## Config (config.json)

Top-level:

- `ConfigVersion`: current format version (2).
- `block`: practice block info and special blocks.
- `positions`: practice world location, exit location, and low-Y fallback.
- `practice`: language, rules, prompts, experience, and scoreboard.

`block`:

- `block.practice`: practice block (`id`, `meta`, `count`).
- `block.pickaxe`: practice pickaxe (`id`, `meta`).
- `block.special.respawn`: respawn checkpoint block id.
- `block.special.stop`: finish block id.
- `block.special.speedUp`: speedup block id.
- `block.special.backSpawn`: back-to-spawn block id.
- `block.special.elevator`: elevator block id.

`positions`:

- `positions.lowY`: below this Y, player is pulled back to respawn.
- `positions.practice`: practice spawn (`x`, `y`, `z`, `level`).
- `positions.exit`: exit location after leaving (`x`, `y`, `z`, `level`).

`practice`:

- `practice.language`: language file in `plugins/BridgingPractise/lang/` (e.g. `en_us`).
- `practice.command`: command name (default `bpractise`).
- `practice.enableCommands`: allowed commands while in practice world.
- `practice.pvpProtect`: block PvP damage.
- `practice.prompt`: show popup prompt.
- `practice.time`: lock world time.
- `practice.weather`: lock world weather (`clear`, `rain`, `thunder`).
- `practice.instaBreak`: clear blocks instantly on death.
- `practice.breakParticle`: show break particles.
- `practice.breakDelay`: delay between block clears (ms).
- `practice.canDrop`: allow item drops.
- `practice.victoryReplace`: block replaced after completing a bridge (`id`, `meta`).
- `practice.fallDamage.enableRespawn`: respawn on heavy fall damage.
- `practice.fallDamage.threshold`: damage threshold to respawn.
- `practice.fallDamage.tip`: show fall damage tips.
- `practice.experience.enable`: enable leveling system.
- `practice.experience.scoreboard`: enable scoreboard display.
- `practice.experience.levelUp`: show level up prompt.
- `practice.experience.tip`: show exp gain prompt.
- `practice.experience.timeEarn`: exp for time played (`enable`, `seconds`, `exp`).
- `practice.experience.blockEarn`: exp for placed blocks (`enable`, `blocks`, `exp`).
- `practice.scoreboard.lines`: scoreboard lines.

Scoreboard placeholders:

- `%player%`, `%level%`, `%lowProgcess%`, `%maxProgcess%`, `%placed%`

## Notes

- Do not modify `plugins/BridgingPractise/cache/` manually.
- Use the `stop` command to shut down the server so the world can restore cleanly.
- Ensure no world named `bpractise` exists before first startup (it will be overwritten).
- Avoid other plugins spawning mobs in the practice world.
