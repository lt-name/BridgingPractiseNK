package cn.ricoco.bridgingpractise;

import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
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
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.plugin.Exp;
import cn.ricoco.bridgingpractise.utils.Utils;

import java.util.Map;

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
            Server.getInstance().getScheduler().scheduleDelayedTask(this.plugin, () -> {
                if (player.isOnline()) {
                    player.teleport(this.plugin.getPluginConfig().getExitPos());
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
            player.setExperience(exp.getExp(), exp.getLevel());
            player.getFoodData().setLevel(playerData.getPlayerHunger());
            ScoreboardUtil.getScoreboard().closeScoreboard(player);
            player.teleport(this.plugin.getPluginConfig().getExitPos());
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
            if (!this.plugin.getPluginConfig().getEnableCommandList().contains(cmd)) {
                e.setCancelled();
                p.sendMessage(Main.language.translateString("cmddisable"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        if (p.getLevel().getName().equals(this.plugin.getPluginConfig().getLevelName()) && !this.plugin.getPluginConfig().isPlayerCanDrop()) {
            e.setCancelled();
            p.sendMessage(Main.language.translateString("cantdrop"));
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
                Utils.ClearBL(p, false);
                return;
            }
            PlayerData playerData = this.plugin.getPlayerData(p);
            int bid = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock().getId();
            //TODO 改为 switch
            if (bid == pluginConfig.getBlockRespawn()) {
                if (!playerData.isPlayeronresp()) {
                    p.sendTitle(Main.language.translateString("setresp"));
                    playerData.setPlayeronresp(true);
                    Block bl = Position.fromObject(new Vector3(pos.x, pos.y - 1, pos.z), pos.level).getLevelBlock();

                    playerData.setPlayerRespawnPos(Position.fromObject(new Vector3(bl.x + 0.5, bl.y + 1, bl.z + 0.5), pos.level));
                    return;
                }
            } else {
                playerData.setPlayeronresp(false);
            }
            if (bid == pluginConfig.getBlockStop()) {
                p.sendTitle(Main.language.translateString("completebridge"));
                Utils.ClearBL(p, true);
                return;
            }
            if (bid == pluginConfig.getBlockBackSpawn()) {
                p.sendTitle(Main.language.translateString("backresp"));
                playerData.setPlayeronresp(true);
                p.teleport(pluginConfig.getSpawnPos());
                return;
            }
            if (bid == pluginConfig.getBlockSpeedup()) {
                Vector3 directionVector = p.getDirectionVector();
                if (p.isSprinting()) {
                    directionVector.setX(directionVector.getX() * 2);
                    directionVector.setZ(directionVector.getZ() * 2);
                }
                p.setMotion(directionVector);
                return;
            }
            if (bid == pluginConfig.getBlockKnockBack()) {
                p.setMotion(new Vector3(-p.getDirectionPlane().x * 0.35, 0.4, -p.getDirectionPlane().y/2 * 0.35));
                return;
            }
            if (bid == pluginConfig.getBlockElevator()) {
                if (!playerData.isPlayerOnElevator()) {
                    playerData.setPlayerOnElevator(true);
                    Position tppos = null;
                    double posx = pos.x, posy = pos.y - 1, posz = pos.z;
                    Level posl = pos.level;
                    for (int i = pos.level.getMinBlockY(); i < pos.level.getMaxBlockY(); i++) {
                        if (Math.abs(posy - i) < 2) {
                            continue;
                        }
                        if (Position.fromObject(new Vector3(posx, i, posz), posl).getLevelBlock().getId() == pluginConfig.getBlockElevator()) {
                            tppos = Position.fromObject(new Vector3(posx, i + 1, posz), posl);
                            break;
                        }
                    }
                    if (tppos == null) {
                        p.sendMessage(Main.language.translateString("tpfailed"));
                    } else {
                        p.teleport(tppos);
                        Server.getInstance().getScheduler().scheduleDelayedTask(this.plugin, () -> {
                            Utils.playSound(p, Sound.MOB_ENDERMEN_PORTAL);
                        }, 5);
                    }
                }
            } else {
                playerData.setPlayerOnElevator(false);
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
                    Utils.displayHurt(player);
                    if (this.plugin.getPluginConfig().isEnableFallDamageRespawn() && this.plugin.getPluginConfig().getFallDamageThreshold() <= event.getDamage()) {
                        Utils.ClearBL(player, false);
                    }
                    if (this.plugin.getPluginConfig().isEnableFallDamageTip()) {
                        player.sendTitle(Main.language.translateString("falldmgtip", event.getDamage()));
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
                e.setCancelled(false);
                Map<Integer, Position> blockPosMap = playerData.getBlockPos();
                blockPosMap.put(blockPosMap.size() + 1, floor);
                playerData.addBlockSecond();
                playerData.addPlayerBlock();
                playerData.addPlace(1);

                Item item = e.getItem();
                PluginConfig.ItemInfo itemInfo = Main.getPlugin().getPluginConfig().getBlockInfo();
                if (item.getId() == itemInfo.getId()
                        && item.getDamage() == itemInfo.getMeta()
                        && item.getCount() <= 1) {
                    Server.getInstance().getScheduler().scheduleDelayedTask(Main.getPlugin(),
                            () -> Utils.addItemToPlayer(player, itemInfo.toItem()),
                            1
                    );
                }
            } else {
                player.sendMessage(Main.language.translateString("cantplaceon"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.level.getName().equals(this.plugin.getPluginConfig().getLevelName())) {
            if (b.getId() == this.plugin.getPluginConfig().getBlockInfo().getId()) {
                Item[] dr = {};
                e.setDrops(dr);
            } else {
                e.setCancelled();
            }
        }
    }
}
