# BridgingPractiseNK

中文 | [English](README.md)

Nukkit 搭路练习插件，提供独立练习世界、特殊方块机制，以及可选的等级/计分板系统。

## 运行环境

- Nukkit
- 依赖: MemoriesOfTime-GameCore

## 安装

1. 将插件 jar 放入 `plugins/`。
2. 安装依赖插件 `MemoriesOfTime-GameCore`。
3. 启动一次服务器以生成 `plugins/BridgingPractise/config.json` 和语言文件。
4. 按需修改 `plugins/BridgingPractise/config.json`。

## 指令

- `/bpractise join` - 加入练习区
- `/bpractise leave` - 离开练习区

指令前缀可在 `practice.command` 中修改。

## 功能

- 练习世界自动备份与还原。
- 特殊方块: 重生点、结束点、回出生点、加速块、电梯块。
- 可选的等级/经验系统与计分板。
- Popup 提示速度/距离等信息。
- PVP 保护、阻止生物生成、练习区指令白名单。

## 配置说明 (config.json)

顶层键:

- `ConfigVersion`: 当前格式版本 (2)。
- `block`: 练习方块与特殊方块。
- `positions`: 练习出生点、退出点、低空回拉。
- `practice`: 语言、规则、提示、经验与计分板。

`block`:

- `block.practice`: 练习方块 (`id`, `meta`, `count`)。
- `block.pickaxe`: 练习镐 (`id`, `meta`)。
- `block.special.respawn`: 重生点方块 id。
- `block.special.stop`: 结束点方块 id。
- `block.special.speedUp`: 加速方块 id。
- `block.special.backSpawn`: 回出生点方块 id。
- `block.special.elevator`: 电梯方块 id。

`positions`:

- `positions.lowY`: 低于此高度会被拉回重生点。
- `positions.practice`: 练习出生点 (`x`, `y`, `z`, `level`)。
- `positions.exit`: 退出后传送位置 (`x`, `y`, `z`, `level`)。

`practice`:

- `practice.language`: 语言文件 `plugins/BridgingPractise/lang/` (如 `en_us`)。
- `practice.command`: 指令前缀 (默认 `bpractise`)。
- `practice.enableCommands`: 练习区允许的指令列表。
- `practice.pvpProtect`: 是否阻止 PVP 伤害。
- `practice.prompt`: 是否显示 Popup 提示。
- `practice.time`: 锁定世界时间。
- `practice.weather`: 锁定世界天气 (`clear`, `rain`, `thunder`)。
- `practice.instaBreak`: 死亡后立即清方块。
- `practice.breakParticle`: 清方块时粒子效果。
- `practice.breakDelay`: 逐步清理的间隔 (ms)。
- `practice.canDrop`: 是否允许丢物品。
- `practice.victoryReplace`: 成功后替换方块 (`id`, `meta`)。
- `practice.fallDamage.enableRespawn`: 跌落伤害过高时回重生点。
- `practice.fallDamage.threshold`: 跌落伤害阈值。
- `practice.fallDamage.tip`: 是否提示跌落伤害。
- `practice.experience.enable`: 启用等级系统。
- `practice.experience.scoreboard`: 启用计分板。
- `practice.experience.levelUp`: 升级提示。
- `practice.experience.tip`: 经验提示。
- `practice.experience.timeEarn`: 在线时间经验 (`enable`, `seconds`, `exp`)。
- `practice.experience.blockEarn`: 放置方块经验 (`enable`, `blocks`, `exp`)。
- `practice.scoreboard.lines`: 计分板内容。

计分板占位符:

- `%player%`, `%level%`, `%lowProgcess%`, `%maxProgcess%`, `%placed%`

## 注意事项

- 不要手动修改 `plugins/BridgingPractise/cache/`。
- 关服请使用 `stop` 命令，避免地图还原异常。
- 首次启动前请确保不存在名为 `bpractise` 的世界。
- 避免其他插件在练习世界生成生物。
