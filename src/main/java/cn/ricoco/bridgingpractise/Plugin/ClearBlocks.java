package cn.ricoco.bridgingpractise.Plugin;

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
            Server.getInstance().getScheduler().scheduleTask(Main.getPlugin(), () -> {
                try {
                    Thread.sleep(variable.configjson.getJSONObject("pra").getInteger("breakdelay"));
                    clearBlocks(blockmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, true);
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
                e.printStackTrace();
            }
        }
    }
}
