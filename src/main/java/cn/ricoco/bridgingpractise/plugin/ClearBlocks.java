package cn.ricoco.bridgingpractise.plugin;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.ricoco.bridgingpractise.Main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClearBlocks {

    public static void clearBlocks(Map<Integer, Position> blockmap, Boolean instabreak) {
        if (blockmap == null || blockmap.isEmpty()) {
            return;
        }

        if (instabreak) {
            clearBlocks(blockmap);
        } else {
            Iterator<Map.Entry<Integer, Position>> iterator = new HashMap<>(blockmap).entrySet().iterator();
            blockmap.clear();
            Server.getInstance().getScheduler().scheduleRepeatingTask(
                    Main.getPlugin(),
                    new Task() {
                        @Override
                        public void onRun(int i) {
                            if (!iterator.hasNext()) {
                                this.cancel();
                                return;
                            }
                            clearBlock(iterator.next().getValue());
                        }
                    },
                    Main.getPlugin().getPluginConfig().getBreakDelay() / 50, //ms -> tick
                    true
            );
        }
    }

    private static void clearBlocks(Map<Integer, Position> blockmap) {
        for (Position pos : blockmap.values()) {
            clearBlock(pos);
        }
        blockmap.clear();
    }

    private static void clearBlock(Position pos) {
        try {
            if (Main.getPlugin().getPluginConfig().isBreakShowParticle()) {
                pos.getLevel().addParticle(new DestroyBlockParticle(new Vector3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5), pos.getLevelBlock()));
            }
            pos.level.setBlockAt((int) pos.x, (int) pos.y, (int) pos.z, 0, 0);
        } catch (Exception e) {
            Main.getPlugin().getLogger().error("Error while clearing block at " + pos.toString(), e);
        }
    }
}
