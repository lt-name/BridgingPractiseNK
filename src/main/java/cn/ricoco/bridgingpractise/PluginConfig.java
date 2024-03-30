package cn.ricoco.bridgingpractise;


import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * 插件配置
 *
 * @author LT_Name
 */
@Getter
public class PluginConfig {

    private final String levelName;

    private final boolean pvpProtect;

    private final BlockInfo blockInfo;

    private final double lowY;

    private final ArrayList<Integer> cantPlaceOn;

    public PluginConfig(Config config) {
        this.levelName = config.getString("pos.pra.l");

        this.pvpProtect = config.getBoolean("pra.pvpprotect");

        Map<Object, Object> block = config.get("block", new LinkedTreeMap<>());
        Map<Object, Object> pra = (Map<Object, Object>) block.getOrDefault("pra", new LinkedTreeMap<>());
        this.blockInfo = new BlockInfo(toInt(pra.get("id")), toInt(pra.get("d")), toInt(pra.get("c")));

        this.lowY = config.getDouble("pos.lowy");

        this.cantPlaceOn = new ArrayList<>();
        this.cantPlaceOn.add(toInt(block.get("stop")));
        this.cantPlaceOn.add(toInt(block.get("res")));
        this.cantPlaceOn.add(toInt(block.get("speedup")));
        this.cantPlaceOn.add(toInt(block.get("backres")));
        this.cantPlaceOn.add(toInt(block.get("elevator")));
    }

    @Data
    public static class BlockInfo {
        private int id;
        private int meta;
        private int count;

        public BlockInfo(int id, int meta, int count) {
            this.id = id;
            this.meta = meta;
            this.count = Math.max(count, 1);
        }

        public Item toItem() {
            return Item.get(id, meta, count);
        }
    }

    public static int toInt(Object object) {
        return new BigDecimal(object.toString()).intValue();
    }

}
