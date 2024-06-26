package cn.ricoco.bridgingpractise;


import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.ricoco.bridgingpractise.utils.Utils;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
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


    private int blockRespawn;
    private int blockStop;
    private int blockBackSpawn;
    private int blockSpeedup;
    private int blockElevator;
    private int blockKnockBack = Block.MELON_BLOCK; //TODO

    public PluginConfig(Config config) {
        this.config = config;
        int configVersion = config.getInt("ConfigVersion", 1);
        switch (configVersion) {
            case 2:
                // this.loadV2();
                break;
            case 1:
            default:
                this.loadV1();
                break;
        }
    }

    public void loadV1() {
        this.language = config.getString("pra.language");

        this.levelName = config.getString("pos.pra.l");

        this.pvpProtect = config.getBoolean("pra.pvpprotect");

        this.lowY = config.getDouble("pos.lowy");

        Map<Object, Object> block = config.get("block", new LinkedTreeMap<>());
        Map<Object, Object> pra = (Map<Object, Object>) block.getOrDefault("pra", new LinkedTreeMap<>());
        this.blockInfo = new ItemInfo(Utils.toInt(pra.get("id")), Utils.toInt(pra.get("d")), Utils.toInt(pra.get("c")));
        Map<Object, Object> pickaxe = (Map<Object, Object>) block.getOrDefault("pickaxe", new LinkedTreeMap<>());
        this.pickaxeInfo = new ItemInfo(Utils.toInt(pickaxe.get("id")), Utils.toInt(pickaxe.get("d")), 1);
        this.victoryReplaceBlock = new ItemInfo(config.getInt("pra.victoryreplace.id", 169), config.getInt("pra.victoryreplace.d", 0), 1);

        this.cantPlaceOn = new ArrayList<>();
        this.cantPlaceOn.add(Utils.toInt(block.get("stop")));
        this.cantPlaceOn.add(Utils.toInt(block.get("res")));
        this.cantPlaceOn.add(Utils.toInt(block.get("speedup")));
        this.cantPlaceOn.add(Utils.toInt(block.get("backres")));
        this.cantPlaceOn.add(Utils.toInt(block.get("elevator")));

        this.command = config.getString("pra.command");
        this.enableCommandList = config.getStringList("pra.enablecmd");

        this.instaBreak = config.getBoolean("pra.instabreak");
        this.breakShowParticle = config.getBoolean("pra.breakparticle");
        this.breakDelay = config.getInt("pra.breakdelay");
        this.playerCanDrop = config.getBoolean("pra.candrop");
        this.enableFallDamageRespawn = config.getBoolean("pra.iffalllagdmg");
        this.fallDamageThreshold = (float) config.getDouble("pra.falllagdmg");
        this.enableFallDamageTip = config.getBoolean("pra.falldmgtip");

        this.spawnPos = new Position(
                config.getInt("pos.pra.x", 0),
                config.getInt("pos.pra.y", 0),
                config.getInt("pos.pra.z", 0),
                Server.getInstance().getLevelByName(levelName)
        );
        this.exitPos = new Position(
                config.getInt("pos.exit.x", 0),
                config.getInt("pos.exit.y", 0),
                config.getInt("pos.exit.z", 0),
                Server.getInstance().getLevelByName(config.getString("pos.exit.l"))
        );

        this.enableLevelSystem = config.getBoolean("pra.exp.enable");

        this.blockRespawn = config.getInt("block.res");
        this.blockStop = config.getInt("block.stop");
        this.blockBackSpawn = config.getInt("block.backres");
        this.blockSpeedup = config.getInt("block.speedup");
        this.blockElevator = config.getInt("block.elevator");
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
