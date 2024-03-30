package cn.ricoco.bridgingpractise.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.variable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerUtils {
    public static void addItemToPlayer(Player player, Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        }
    }

    public static void ClearBL(@NotNull Player player, Boolean repb) {
        PlayerData playerData = Main.getPlugin().getPlayerData(player);
        player.teleport(playerData.getPlayerResPos());
        Map<Integer, Position> blockmap = playerData.getBlockPos();
        int ble = blockmap.size();
        if (repb) {
            LevelUtils.replaceBl(blockmap);
        }
        ClearBlocks.clearBlocks(blockmap, variable.configjson.getJSONObject("pra").getBoolean("instabreak"));
        if (playerData.getBlockMax() < ble) {
            playerData.setBlockMax(ble);
        }
        playerData.setPlayeronresp(true);
    }
}
