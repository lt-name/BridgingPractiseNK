package cn.ricoco.bridgingpractise;


import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件配置
 *
 * @author LT_Name
 */
@Getter
public class PluginConfig {

    private final Config config;

    private String language;

    private String levelName;

    private boolean pvpProtect;

    private double lowY;

    private ItemInfo blockInfo;
    private ItemInfo pickaxeInfo;
    /**
     * 玩家搭路成功后替换的方块
     */
    private ItemInfo victoryReplaceBlock;
    private ArrayList<Integer> cantPlaceOn;

    private String command;
    private List<String> enableCommandList;

    /**
     * 死亡后方块是否直接清除
     */
    private boolean instaBreak;
    /**
     * 清理玩家放置的方块时是否显示破坏方块粒子
     */
    private boolean breakShowParticle;
    /**
     * 逐渐清除方块时清除单个方块的延时(ms)
     */
    private int breakDelay;
    /**
     * 玩家是否可以丢出物品
     */
    private boolean playerCanDrop;
    /**
     * 是否受到超过阈值的跌落伤害时回到出生点
     */
    private boolean enableFallDamageRespawn;
    /**
     * 掉落伤害阈值
     */
    private float fallDamageThreshold;
    /**
     * 是否在受到掉落伤害时向玩家发出提示
     */
    private boolean enableFallDamageTip;

    /**
     * 练习区默认重生点
     */
    private Position spawnPos;
    /**
     * 退出后的位置（插件首次加载时读取默认世界出生点）
     */
    private Position exitPos;

    /**
     * 是否启用等级系统
     */
    private boolean enableLevelSystem;
    private boolean expScoreboard;
    private boolean expLevelUp;
    private boolean expTip;
    private List<String> scoreboard;
    private boolean timeEarnEnable;
    private int timeEarnSec;
    private int timeEarnExp;
    private boolean blockEarnEnable;
    private int blockEarnBlocks;
    private int blockEarnExp;

    private boolean prompt;
    private int time;
    private String weather;

    private int blockRespawn;
    private int blockStop;
    private int blockBackSpawn;
    private int blockSpeedup;
    private int blockElevator;
    private int blockKnockBack = Block.MELON_BLOCK; //TODO

    public PluginConfig(Config config) {
        this.config = config;
        int configVersion = config.getInt("ConfigVersion", 1);
        if (configVersion < 2) {
            this.migrateV1ToV2();
            configVersion = 2;
        }

        this.load();
    }

    public void load() {
        this.language = config.getString("practice.language");

        this.levelName = config.getString("positions.practice.level");

        this.pvpProtect = config.getBoolean("practice.pvpProtect");

        this.lowY = config.getDouble("positions.lowY");

        this.blockInfo = new ItemInfo(
                config.getInt("block.practice.id"),
                config.getInt("block.practice.meta"),
                config.getInt("block.practice.count")
        );
        this.pickaxeInfo = new ItemInfo(
                config.getInt("block.pickaxe.id"),
                config.getInt("block.pickaxe.meta"),
                1
        );
        this.victoryReplaceBlock = new ItemInfo(
                config.getInt("practice.victoryReplace.id", 169),
                config.getInt("practice.victoryReplace.meta", 0),
                1
        );

        this.cantPlaceOn = new ArrayList<>();
        this.cantPlaceOn.add(config.getInt("block.special.stop"));
        this.cantPlaceOn.add(config.getInt("block.special.respawn"));
        this.cantPlaceOn.add(config.getInt("block.special.speedUp"));
        this.cantPlaceOn.add(config.getInt("block.special.backSpawn"));
        this.cantPlaceOn.add(config.getInt("block.special.elevator"));

        this.command = config.getString("practice.command");
        this.enableCommandList = config.getStringList("practice.enableCommands");

        this.instaBreak = config.getBoolean("practice.instaBreak");
        this.breakShowParticle = config.getBoolean("practice.breakParticle");
        this.breakDelay = config.getInt("practice.breakDelay");
        this.playerCanDrop = config.getBoolean("practice.canDrop");
        this.enableFallDamageRespawn = config.getBoolean("practice.fallDamage.enableRespawn");
        this.fallDamageThreshold = (float) config.getDouble("practice.fallDamage.threshold");
        this.enableFallDamageTip = config.getBoolean("practice.fallDamage.tip");

        this.spawnPos = new Position(
                config.getDouble("positions.practice.x", 0),
                config.getDouble("positions.practice.y", 0),
                config.getDouble("positions.practice.z", 0),
                Server.getInstance().getLevelByName(levelName)
        );
        this.exitPos = new Position(
                config.getDouble("positions.exit.x", 0),
                config.getDouble("positions.exit.y", 0),
                config.getDouble("positions.exit.z", 0),
                Server.getInstance().getLevelByName(config.getString("positions.exit.level"))
        );

        this.enableLevelSystem = config.getBoolean("practice.experience.enable");
        this.expScoreboard = config.getBoolean("practice.experience.scoreboard");
        this.expLevelUp = config.getBoolean("practice.experience.levelUp");
        this.expTip = config.getBoolean("practice.experience.tip");
        this.scoreboard = config.getStringList("practice.scoreboard.lines");
        this.timeEarnEnable = config.getBoolean("practice.experience.timeEarn.enable");
        this.timeEarnSec = config.getInt("practice.experience.timeEarn.seconds");
        this.timeEarnExp = config.getInt("practice.experience.timeEarn.exp");
        this.blockEarnEnable = config.getBoolean("practice.experience.blockEarn.enable");
        this.blockEarnBlocks = config.getInt("practice.experience.blockEarn.blocks");
        this.blockEarnExp = config.getInt("practice.experience.blockEarn.exp");

        this.prompt = config.getBoolean("practice.prompt");
        this.time = config.getInt("practice.time");
        this.weather = config.getString("practice.weather");

        this.blockRespawn = config.getInt("block.special.respawn");
        this.blockStop = config.getInt("block.special.stop");
        this.blockBackSpawn = config.getInt("block.special.backSpawn");
        this.blockSpeedup = config.getInt("block.special.speedUp");
        this.blockElevator = config.getInt("block.special.elevator");
    }

    private void migrateV1ToV2() {
        LinkedHashMap<String, Object> root = new LinkedHashMap<>();
        root.put("ConfigVersion", 2);

        Map<String, Object> block = new LinkedHashMap<>();
        Map<String, Object> practiceBlock = new LinkedHashMap<>();
        practiceBlock.put("id", config.getInt("block.pra.id"));
        practiceBlock.put("meta", config.getInt("block.pra.d"));
        practiceBlock.put("count", config.getInt("block.pra.c"));
        block.put("practice", practiceBlock);

        Map<String, Object> pickaxe = new LinkedHashMap<>();
        pickaxe.put("id", config.getInt("block.pickaxe.id"));
        pickaxe.put("meta", config.getInt("block.pickaxe.d"));
        block.put("pickaxe", pickaxe);

        Map<String, Object> special = new LinkedHashMap<>();
        special.put("respawn", config.getInt("block.res"));
        special.put("stop", config.getInt("block.stop"));
        special.put("speedUp", config.getInt("block.speedup"));
        special.put("backSpawn", config.getInt("block.backres"));
        special.put("elevator", config.getInt("block.elevator"));
        block.put("special", special);
        root.put("block", block);

        Map<String, Object> positions = new LinkedHashMap<>();
        positions.put("lowY", config.getDouble("pos.lowy"));
        Map<String, Object> practicePos = new LinkedHashMap<>();
        practicePos.put("x", config.getDouble("pos.pra.x"));
        practicePos.put("y", config.getDouble("pos.pra.y"));
        practicePos.put("z", config.getDouble("pos.pra.z"));
        practicePos.put("level", config.getString("pos.pra.l"));
        positions.put("practice", practicePos);
        Map<String, Object> exitPos = new LinkedHashMap<>();
        exitPos.put("x", config.getDouble("pos.exit.x"));
        exitPos.put("y", config.getDouble("pos.exit.y"));
        exitPos.put("z", config.getDouble("pos.exit.z"));
        exitPos.put("level", config.getString("pos.exit.l"));
        positions.put("exit", exitPos);
        root.put("positions", positions);

        Map<String, Object> practice = new LinkedHashMap<>();
        practice.put("language", config.getString("pra.language"));
        practice.put("pvpProtect", config.getBoolean("pra.pvpprotect"));
        practice.put("prompt", config.getBoolean("pra.prompt"));
        practice.put("time", config.getInt("pra.time"));
        practice.put("weather", config.getString("pra.weather"));
        practice.put("command", config.getString("pra.command"));
        practice.put("enableCommands", config.getStringList("pra.enablecmd"));
        practice.put("instaBreak", config.getBoolean("pra.instabreak"));
        practice.put("breakParticle", config.getBoolean("pra.breakparticle"));
        practice.put("breakDelay", config.getInt("pra.breakdelay"));
        practice.put("canDrop", config.getBoolean("pra.candrop"));

        Map<String, Object> victoryReplace = new LinkedHashMap<>();
        victoryReplace.put("id", config.getInt("pra.victoryreplace.id", 169));
        victoryReplace.put("meta", config.getInt("pra.victoryreplace.d", 0));
        practice.put("victoryReplace", victoryReplace);

        Map<String, Object> fallDamage = new LinkedHashMap<>();
        fallDamage.put("enableRespawn", config.getBoolean("pra.iffalllagdmg"));
        fallDamage.put("threshold", config.getDouble("pra.falllagdmg"));
        fallDamage.put("tip", config.getBoolean("pra.falldmgtip"));
        practice.put("fallDamage", fallDamage);

        Map<String, Object> experience = new LinkedHashMap<>();
        experience.put("enable", config.getBoolean("pra.exp.enable"));
        experience.put("scoreboard", config.getBoolean("pra.exp.scoreboard"));
        experience.put("levelUp", config.getBoolean("pra.exp.levelup"));
        experience.put("tip", config.getBoolean("pra.exp.getexp"));
        Map<String, Object> timeEarn = new LinkedHashMap<>();
        timeEarn.put("enable", config.getBoolean("pra.exp.timeearn.enable"));
        timeEarn.put("seconds", config.getInt("pra.exp.timeearn.sec"));
        timeEarn.put("exp", config.getInt("pra.exp.timeearn.exp"));
        experience.put("timeEarn", timeEarn);
        Map<String, Object> blockEarn = new LinkedHashMap<>();
        blockEarn.put("enable", config.getBoolean("pra.exp.blockearn.enable"));
        blockEarn.put("blocks", config.getInt("pra.exp.blockearn.bls"));
        blockEarn.put("exp", config.getInt("pra.exp.blockearn.exp"));
        experience.put("blockEarn", blockEarn);
        practice.put("experience", experience);

        Map<String, Object> scoreboard = new LinkedHashMap<>();
        scoreboard.put("lines", config.getStringList("pra.scoreboard"));
        practice.put("scoreboard", scoreboard);

        root.put("practice", practice);

        config.setAll(root);
        config.save();
    }

    @Data
    public static class ItemInfo {
        private int id;
        private int meta;
        private int count;

        public ItemInfo(int id, int meta, int count) {
            this.id = id;
            this.meta = meta;
            this.count = Math.max(count, 1);
        }

        public Item toItem() {
            return Item.get(id, meta, count);
        }
    }

}
