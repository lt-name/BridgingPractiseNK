package cn.ricoco.bridgingpractise.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author LT_Name
 */
public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static int toInt(Object object) {
        return new BigDecimal(object.toString()).intValue();
    }

    public static void addItemToPlayer(@NotNull Player player, @NotNull Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        }
    }

    public static void ClearBL(@NotNull Player player, boolean replaceBlocks) {
        PlayerData playerData = Main.getPlugin().getPlayerData(player);
        player.teleport(playerData.getPlayerRespawnPos());
        Map<Integer, Position> blockmap = playerData.getBlockPos();
        int ble = blockmap.size();
        if (replaceBlocks) {
            LevelUtils.replaceBl(blockmap);
        }
        ClearBlocks.clearBlocks(blockmap, Main.getPlugin().getPluginConfig().isInstaBreak());
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

    public static void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        packet.x = player.getFloorX();
        packet.y = player.getFloorY();
        packet.z = player.getFloorZ();
        player.dataPacket(packet);
    }

}
