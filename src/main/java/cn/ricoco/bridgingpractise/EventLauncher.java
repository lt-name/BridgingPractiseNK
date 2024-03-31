package cn.ricoco.bridgingpractise;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
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
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.plugin.Exp;
import cn.ricoco.bridgingpractise.utils.EntityUtils;
import cn.ricoco.bridgingpractise.utils.PlayerUtils;
import cn.ricoco.bridgingpractise.utils.ScoreboardUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import static cn.ricoco.bridgingpractise.utils.PlayerUtils.ClearBL;

public class EventLauncher implements Listener {
    private final Main plugin;

    public EventLauncher(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.plugin.saveResource("resources/player.json", "/players/" + player.getName() + ".json", false);
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
        Player player = e.getPlayer();
        if (player.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            String playerName = player.getName();
            PlayerData playerData = this.plugin.getPlayerData(player);
            
            ClearBlocks.clearBlocks(playerData.getBlockPos(), true);

            player.setGamemode(playerData.getPlayerGameMode());
            player.getInventory().setContents(playerData.getPlayerInv());
            Exp exp = playerData.getPlayerLevel();
            player.setExperience(exp.getExp(), exp.getLv());
            player.getFoodData().setLevel(playerData.getPlayerHunger());
            ScoreboardUtils.removeSB(player);
            player.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("z")), Server.getInstance().getLevelByName(variable.configjson.getJSONObject("pos").getJSONObject("exit").getString("l"))));
        }

        PlayerData remove = this.plugin.getPlayerDataMap().remove(player);
        if (remove != null) {
            remove.save();
            remove.clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (p.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            String cmd = e.getMessage().substring(1).split(" ")[0];
            if (!variable.configjson.getJSONObject("pra").getJSONArray("enablecmd").contains(cmd)) {
                e.setCancelled();
                p.sendMessage(Main.languageConfig.getString("cmddisable"));
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
            p.sendMessage(Main.languageConfig.getString("cantdrop"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        Position pos = p.getPosition();
        PluginConfig pluginConfig = this.plugin.getPluginConfig();
        if (pos.getLevel().getName().equals(pluginConfig.getLevelName())) {
            if (pos.getY() < pluginConfig.getLowY()) {
                ClearBL(p, false);
                return;
            }
            PlayerData playerData = this.plugin.getPlayerData(p);
            int bid = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock().getId();
            if (bid == variable.configjson.getJSONObject("block").getInteger("res")) {
                if (!playerData.isPlayeronresp()) {
                    p.sendTitle(Main.languageConfig.getString("setresp"));
                    playerData.setPlayeronresp(true);
                    Block bl = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock();

                    playerData.setPlayerResPos(Position.fromObject(new Vector3(bl.x + 0.5, bl.y + 1, bl.z + 0.5), pos.level));
                    return;
                }
            } else {
                playerData.setPlayeronresp(false);
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("stop")) {
                p.sendTitle(Main.languageConfig.getString("completebridge"));
                ClearBL(p, true);
                return;
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("backres")) {
                p.sendTitle(Main.languageConfig.getString("backresp"));
                playerData.setPlayeronresp(true);
                p.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("z")), Server.getInstance().getLevelByName(pluginConfig.getLevelName())));
                return;
            }
            if (bid == variable.configjson.getJSONObject("block").getInteger("speedup")) {
                p.setMotion(new Vector3(p.getDirectionVector().x, 0, p.getDirectionVector().z));
                return;
            }
            int eid = variable.configjson.getJSONObject("block").getInteger("elevator");
            if (bid == eid) {
                if (!playerData.isPlayeronelevator()) {
                    playerData.setPlayeronelevator(true);
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
                        p.sendMessage(Main.languageConfig.getString("tpfailed"));
                    } else {
                        p.teleport(tppos);
                    }
                }
            } else {
                playerData.setPlayeronelevator(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Position pos = player.getPosition();
            if (pos.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName())) {
                if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                    if (this.plugin.getPluginConfig().isPvpProtect()) {
                        event.setCancelled();
                    } else {
                        event.setDamage(0);
                    }
                } else {
                    event.setCancelled();
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    JSONObject json = variable.configjson.getJSONObject("pra");
                    EntityUtils.displayHurt(player);
                    if (json.getBoolean("iffalllagdmg") && json.getFloat("falllagdmg") <= event.getDamage()) {
                        ClearBL(player, false);
                    }
                    if (json.getBoolean("falldmgtip")) {
                        player.sendTitle(Main.languageConfig.getString("falldmgtip").replaceAll("%1", event.getDamage() + ""));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Block b = e.getBlock();
        if (b.level.getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            Player player = e.getPlayer();
            PlayerData playerData = this.plugin.getPlayerData(player);

            e.setCancelled();
            int bid = b.getSide(BlockFace.DOWN).getId();
            Position floor = b.floor();
            if (!this.plugin.getPluginConfig().getCantPlaceOn().contains(bid) && !this.plugin.getPluginConfig().getCantPlaceOn().contains(Position.fromObject(floor.add(0, -2, 0), b.level).getLevelBlock().getId())) {
                //b.level.setBlockAt((int) b.x, (int) b.y, (int) b.z, b.getId(), b.getDamage());
                e.setCancelled(false);
                Map<Integer, Position> blockPosMap = playerData.getBlockPos();
                blockPosMap.put(blockPosMap.size() + 1, floor);
                playerData.addBlockSecond();
                playerData.addPlayerBlock();
                playerData.addPlace(1);

                Item item = e.getItem();
                PluginConfig.BlockInfo blockInfo = Main.getPlugin().getPluginConfig().getBlockInfo();
                if (item.getId() == blockInfo.getId()
                        && item.getDamage() == blockInfo.getMeta()
                        && item.getCount() <= 1) {
                    Server.getInstance().getScheduler().scheduleDelayedTask(Main.getPlugin(),
                            () -> PlayerUtils.addItemToPlayer(player, blockInfo.toItem()),
                            1
                    );
                }
            } else {
                player.sendMessage(Main.languageConfig.getString("cantplaceon"));
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
