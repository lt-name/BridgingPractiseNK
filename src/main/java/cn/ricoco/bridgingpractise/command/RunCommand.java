package cn.ricoco.bridgingpractise.command;

import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
import cn.lanink.gamecore.utils.Tips;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.PluginConfig;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.plugin.Exp;
import cn.ricoco.bridgingpractise.utils.Utils;

public class RunCommand extends Command {
    public RunCommand(String name, String description) {
        super(name, description);

        this.commandParameters.clear();
        this.commandParameters.put("join", new CommandParameter[] {
                CommandParameter.newEnum("join", new String[]{"join"})
        });
        this.commandParameters.put("leave", new CommandParameter[] {
                CommandParameter.newEnum("leave", new String[]{"leave"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(Main.language.translateString("notplayer"));
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(Main.language.translateString("usage", Main.getPlugin().getPluginConfig().getCommand()));
            return false;
        }
        Player player = (Player) sender;
        String levelName = player.getPosition().getLevel().getName();
        String playerName = player.getName();
        PlayerData playerData = Main.getPlugin().getPlayerData(player);
        PluginConfig pluginConfig = Main.getPlugin().getPluginConfig();
        switch (args[0]) {
            case "join":
                if (!levelName.equals(pluginConfig.getLevelName())) {
                    playerData.setPlayerGameMode(player.getGamemode());
                    playerData.setPlayerInv(player.getInventory().getContents());
                    playerData.setPlayerHunger(player.getFoodData().getLevel());
                    playerData.setBlockSecond(0);
                    playerData.setBlockMax(0);
                    player.getInventory().clearAll();
                    Utils.addItemToPlayer(player, pluginConfig.getBlockInfo().toItem());
                    Utils.addItemToPlayer(player, pluginConfig.getPickaxeInfo().toItem());
                    Position pos = pluginConfig.getSpawnPos();
                    playerData.setPlayerRespawnPos(pos);
                    playerData.setPlayeronresp(false);
                    playerData.setPlayerOnElevator(false);
                    playerData.setPlayerLevel(new Exp(player.getExperience(), player.getExperienceLevel()));
                    playerData.setPlayerBlock(0);
                    playerData.setPlayerTime(0);
                    player.setNameTag("§7[§6" + playerData.getLevel() + "§7]§f" + player.getName());
                    if (pluginConfig.isEnableLevelSystem()) {
                        player.setExperience(playerData.getExp(), playerData.getLevel());
                    } else {
                        player.setExperience(0);
                    }
                    sender.sendMessage(Main.language.translateString("joinedarena"));
                    player.teleport(pos);
                    player.setGamemode(0);
                    Tips.closeTipsShow(pos.getLevel().getFolderName(), player);
                } else {
                    sender.sendMessage(Main.language.translateString("stillinarena"));
                }
                break;
            case "leave":
                if (levelName.equals(Main.getPlugin().getPluginConfig().getLevelName())) {
                    ClearBlocks.clearBlocks(playerData.getBlockPos(), true);
                    player.setGamemode(playerData.getPlayerGameMode());
                    player.getInventory().setContents(playerData.getPlayerInv());
                    Exp exp = playerData.getPlayerLevel();
                    player.setExperience(exp.getExp(), exp.getLevel());
                    player.getFoodData().setLevel(playerData.getPlayerHunger());
                    player.setNameTag(player.getName());
                    ScoreboardUtil.getScoreboard().closeScoreboard(player);
                    Tips.removeTipsConfig(player.getLevel().getFolderName(), player);
                    player.teleport(pluginConfig.getExitPos());
                    sender.sendMessage(Main.language.translateString("leavearena"));
                    PlayerData remove = Main.getPlugin().getPlayerDataMap().remove(player);
                    if (remove != null) {
                        remove.save();
                        remove.clear();
                    }
                } else {
                    sender.sendMessage(Main.language.translateString("notinarena"));
                }
                break;
            default:
                sender.sendMessage(Main.language.translateString("usage", Main.getPlugin().getPluginConfig().getCommand()));
        }
        return false;
    }
}