package cn.ricoco.bridgingpractise.plugin;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.variable;

import java.util.Map;

public class ClearBlocks {

    public static void clearBlocks(Map<Integer, Position> blockmap, Boolean instabreak) {
        if (blockmap == null || blockmap.isEmpty()) {
            return;
        }

        if (instabreak) {
            clearBlocks(blockmap);
        } else {
            Server.getInstance().getScheduler().scheduleDelayedTask(
                    Main.getPlugin(),
                    () -> clearBlocks(blockmap),
                    variable.configjson.getJSONObject("pra").getInteger("breakdelay")/50,
                    true
            );
        }
    }

    private static void clearBlocks(Map<Integer, Position> blockmap) {
        for (Position pos : blockmap.values()) {
            try {
                if (variable.configjson.getJSONObject("pra").getBoolean("breakparticle")) {
                    pos.getLevel().addParticle(new DestroyBlockParticle(new Vector3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5), pos.getLevelBlock()));
                }
                pos.level.setBlockAt((int) pos.x, (int) pos.y, (int) pos.z, 0, 0);
            } catch (Exception e) {
                Main.getPlugin().getLogger().error("Error while clearing block at " + pos.toString(), e);
            }
        }
        blockmap.clear();
    }
}
