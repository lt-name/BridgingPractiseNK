package cn.ricoco.bridgingpractise;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.ricoco.bridgingpractise.Plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.Plugin.Exp;
import cn.ricoco.bridgingpractise.Utils.EntityUtils;
import cn.ricoco.bridgingpractise.Utils.FileUtils;
import cn.ricoco.bridgingpractise.Utils.ScoreboardUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static cn.ricoco.bridgingpractise.Utils.PlayerUtils.ClearBL;

public class EventLauncher implements Listener {
    private final Main plugin;

    public EventLauncher(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String playerJson = "/players/" + player.getName() + ".json";
        this.plugin.saveResource("resources/player.json", playerJson, false);
        variable.playerLevelJSON.put(player.getName(), JSONObject.parseObject(FileUtils.readFile(Main.getPlugin().getDataFolder() + playerJson)));
        variable.blockpos.put(player.getName(), new HashMap<>());
        if (player.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            Position pos = Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("z")), Server.getInstance().getLevelByName(variable.configjson.getJSONObject("pos").getJSONObject("exit").getString("l")));
            Server.getInstance().getScheduler().scheduleDelayedTask(this.plugin, () -> {
                if (player.isOnline()) {
                    player.teleport(pos);
                }
            }, 60);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (p.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            String playerName = p.getName();

            ClearBlocks.clearBlocks(variable.blockpos.remove(playerName), true);

            p.setGamemode(variable.playergamemode.get(playerName));
            p.getInventory().setContents(variable.playerinv.remove(playerName));
            variable.playerresp.remove(playerName);
            variable.blocksecond.remove(playerName);
            variable.blockmax.remove(playerName);
            variable.playeronresp.remove(playerName);
            variable.playeronelevator.remove(playerName);
            variable.playerBlock.remove(playerName);
            variable.playerTime.remove(playerName);
            Exp exp = variable.playerLevel.remove(playerName);
            p.setExperience(exp.getExp(), exp.getLv());
            FileUtils.writeFile(this.plugin.getDataFolder() + "/players/" + playerName + ".json", JSONObject.toJSONString(variable.playerLevelJSON.remove(playerName)));
            p.getFoodData().setLevel(variable.playerhunger.remove(playerName));
            ScoreboardUtils.removeSB(p);
            p.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("z")), Server.getInstance().getLevelByName(variable.configjson.getJSONObject("pos").getJSONObject("exit").getString("l"))));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (p.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            String cmd = e.getMessage().substring(1).split(" ")[0];
            if (!variable.configjson.getJSONObject("pra").getJSONArray("enablecmd").contains(cmd)) {
                e.setCancelled();
                p.sendMessage(variable.langjson.getString("cmddisable"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        if (p.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName()) && !variable.configjson.getJSONObject("pra").getBoolean("candrop")) {
            e.setCancelled();
            p.sendMessage(variable.langjson.getString("cantdrop"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        Position pos = p.getPosition();
        if (pos.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            if (pos.getY() < variable.lowy) {
                ClearBL(p, false);
                return;
            }
            int bid = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock().getId();
            if (bid == variable.configjson.getJSONObject("block").getInteger("res")) {
                if (!variable.playeronresp.containsKey(p.getName()) || !variable.playeronresp.get(p.getName())) {
                    p.sendTitle(variable.langjson.getString("setresp"));
                    variable.playeronresp.put(p.getName(), true);
                    Block bl = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock();
                    variable.playerresp.put(p.getName(), Position.fromObject(new Vector3(bl.x + 0.5, bl.y + 1, bl.z + 0.5), pos.level));
                    return;
                }
            } else {
                variable.playeronresp.put(p.getName(), false);
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("stop")) {
                p.sendTitle(variable.langjson.getString("completebridge"));
                ClearBL(p, true);
                return;
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("backres")) {
                p.sendTitle(variable.langjson.getString("backresp"));
                variable.playeronresp.put(p.getName(), true);
                p.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("z")), Server.getInstance().getLevelByName(this.plugin.getPluginConfig().getLevelName())));
                return;
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("speedup")) {
                p.setMotion(new Vector3(p.getDirectionVector().x, 0, p.getDirectionVector().z));
                return;
            }
            int eid = variable.configjson.getJSONObject("block").getInteger("elevator");
            if (bid == eid) {
                if (!variable.playeronelevator.get(p.getName())) {
                    variable.playeronelevator.put(p.getName(), true);
                    Position tppos = null;
                    double posx = pos.x, posy = pos.y - 1, posz = pos.z;
                    Level posl = pos.level;
                    for (int i = 0; i < 255; i++) {
                        if (i == posy) {
                            continue;
                        }
                        if (Position.fromObject(new Vector3(posx, i, posz), posl).getLevelBlock().getId() == eid) {
                            tppos = Position.fromObject(new Vector3(posx, i + 1, posz), posl);
                            break;
                        }
                    }
                    if (tppos == null) {
                        p.sendMessage(variable.langjson.getString("tpfailed"));
                    } else {
                        p.teleport(tppos);
                    }
                }
            } else {
                variable.playeronelevator.put(p.getName(), false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Position pos = p.getPosition();
            if (pos.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
                String c = e.getCause().toString();
                e.setCancelled();
                if (c.equals("FALL")) {
                    JSONObject json = variable.configjson.getJSONObject("pra");
                    EntityUtils.displayHurt(p);
                    if (json.getBoolean("iffalllagdmg") && json.getFloat("falllagdmg") <= e.getDamage()) {
                        ClearBL(p, false);
                    }
                    if (json.getBoolean("falldmgtip")) {
                        p.sendTitle(variable.langjson.getString("falldmgtip").replaceAll("%1", e.getDamage() + ""));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Entity en = e.getEntity();
        if (variable.configjson.getJSONObject("pra").getBoolean("pvpprotect") && en.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            e.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Block b = e.getBlock();
        if (b.level.getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            Player p = e.getPlayer();
            e.setCancelled();
            int bid = b.getSide(BlockFace.DOWN).getId();
            Position floor = b.floor();
            if (!variable.cantPlaceOn.contains(bid) && !variable.cantPlaceOn.contains(Position.fromObject(floor.add(0, -2, 0), b.level).getLevelBlock().getId())) {
                //b.level.setBlockAt((int) b.x, (int) b.y, (int) b.z, b.getId(), b.getDamage());
                e.setCancelled(false);
                Map<Integer, Position> blockPosMap = variable.blockpos.get(p.getName());
                blockPosMap.put(blockPosMap.size() + 1, floor);
                variable.blocksecond.put(p.getName(), variable.blocksecond.get(p.getName()) + 1);
                variable.playerBlock.put(p.getName(), variable.playerBlock.get(p.getName()) + 1);
                JSONObject plj = variable.playerLevelJSON.get(p.getName());
                plj.put("place", plj.getInteger("place") + 1);
                variable.playerLevelJSON.put(p.getName(), plj);
            } else {
                p.sendMessage(variable.langjson.getString("cantplaceon"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.level.getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            if (b.getId() == variable.configjson.getJSONObject("block").getJSONObject("pra").getInteger("id")) {
                Item[] dr = {};
                e.setDrops(dr);
            } else {
                e.setCancelled();
            }
        }
    }
}
