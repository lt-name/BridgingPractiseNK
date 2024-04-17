package cn.ricoco.bridgingpractise;


import cn.nukkit.Server;
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

    private final String language;

    private final String levelName;

    private final boolean pvpProtect;

    private final double lowY;

    private final ItemInfo blockInfo;
    private final ItemInfo pickaxeInfo;
    /**
     * 玩家搭路成功后替换的方块
     */
    private final ItemInfo victoryReplaceBlock;
    private final ArrayList<Integer> cantPlaceOn;

    private final String command;
    private final List<String> enableCommandList;

    /**
     * 死亡后方块是否直接清除
     */
    private final boolean instaBreak;
    /**
     * 清理玩家放置的方块时是否显示破坏方块粒子
     */
    private final boolean breakShowParticle;
    /**
     * 逐渐清除方块时清除单个方块的延时(ms)
     */
    private final int breakDelay;

    /**
     * 练习区默认重生点
     */
    private final Position spawnPos;
    /**
     * 退出后的位置（插件首次加载时读取默认世界出生点）
     */
    private final Position exitPos;

    /**
     * 是否启用等级系统
     */
    private final boolean enableLevelSystem;

    public PluginConfig(Config config) {
        this.language = config.getString("pra.language");

        this.levelName = config.getString("pos.pra.l");

        this.pvpProtect = config.getBoolean("pra.pvpprotect");

        this.lowY = config.getDouble("pos.lowy");

        Map<Object, Object> block = config.get("block", new LinkedTreeMap<>());
        Map<Object, Object> pra = (Map<Object, Object>) block.getOrDefault("pra", new LinkedTreeMap<>());
        this.blockInfo = new ItemInfo(Utils.toInt(pra.get("id")), Utils.toInt(pra.get("d")), Utils.toInt(pra.get("c")));
        Map<Object, Object> pickaxe = (Map<Object, Object>) block.getOrDefault("pickaxe", new LinkedTreeMap<>());
        this.pickaxeInfo = new ItemInfo(Utils.toInt(pra.get("id")), Utils.toInt(pra.get("d")), 1);
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
