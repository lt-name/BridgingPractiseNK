package cn.ricoco.bridgingpractise.Utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.ricoco.bridgingpractise.Plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.variable;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtils {
    public static void addItemToPlayer(Player player, Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        }
    }

    public static void ClearBL(Player p, Boolean repb) {
        p.teleport(variable.playerresp.get(p.getName()));
        Map<Integer, Position> blockmap = variable.blockpos.get(p.getName());
        int ble = blockmap.size();
        if (repb) {
            LevelUtils.replaceBl(blockmap);
        }
        ClearBlocks.clearBlocks(variable.blockpos.remove(p.getName()), variable.configjson.getJSONObject("pra").getBoolean("instabreak"));
        Map<Integer, Position> m = new HashMap<>();
        if (variable.blockmax.getOrDefault(p.getName(), 0) < ble) {
            variable.blockmax.put(p.getName(), ble);
        }
        variable.blockpos.put(p.getName(), m);
        variable.playeronresp.put(p.getName(), true);
    }
}
