package cn.ricoco.bridgingpractise;


import cn.nukkit.item.Item;
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

    public PluginConfig(Config config) {
        this.language = config.getString("pra.language");

        this.levelName = config.getString("pos.pra.l");

        this.pvpProtect = config.getBoolean("pra.pvpprotect");

        this.lowY = config.getDouble("pos.lowy");

        Map<Object, Object> block = config.get("block", new LinkedTreeMap<>());
        Map<Object, Object> pra = (Map<Object, Object>) block.getOrDefault("pra", new LinkedTreeMap<>());
        this.blockInfo = new ItemInfo(Utils.toInt(pra.get("id")), Utils.toInt(pra.get("d")), Utils.toInt(pra.get("c")));

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
