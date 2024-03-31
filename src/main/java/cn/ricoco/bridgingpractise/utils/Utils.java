package cn.ricoco.bridgingpractise.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.variable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author LT_Name
 */
public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static void addItemToPlayer(@NotNull Player player, @NotNull Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        }
    }

    public static void ClearBL(@NotNull Player player, boolean replaceBlocks) {
        PlayerData playerData = Main.getPlugin().getPlayerData(player);
        player.teleport(playerData.getPlayerResPos());
        Map<Integer, Position> blockmap = playerData.getBlockPos();
        int ble = blockmap.size();
        if (replaceBlocks) {
            LevelUtils.replaceBl(blockmap);
        }
        ClearBlocks.clearBlocks(blockmap, variable.configjson.getJSONObject("pra").getBoolean("instabreak"));
        if (playerData.getBlockMax() < ble) {
            playerData.setBlockMax(ble);
        }
        playerData.setPlayeronresp(true);
    }

    public static void displayHurt(Player e) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = e.getId();
        pk.event = EntityEventPacket.HURT_ANIMATION;
        e.getViewers().values().forEach((player -> player.dataPacket(pk)));
    }

}
