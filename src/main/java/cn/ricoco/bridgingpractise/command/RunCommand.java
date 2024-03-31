package cn.ricoco.bridgingpractise.command;

import cn.lanink.gamecore.utils.Tips;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.plugin.Exp;
import cn.ricoco.bridgingpractise.utils.FileUtils;
import cn.ricoco.bridgingpractise.utils.ScoreboardUtils;
import cn.ricoco.bridgingpractise.utils.Utils;
import cn.ricoco.bridgingpractise.variable;
import com.alibaba.fastjson.JSONObject;

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
            sender.sendMessage(Main.language.translateString("usage", variable.configjson.getJSONObject("pra").getString("command")));
            return false;
        }
        Player player = (Player) sender;
        String levelName = player.getPosition().getLevel().getName();
        String playerName = player.getName();
        PlayerData playerData = Main.getPlugin().getPlayerData(player);
        switch (args[0]) {
            case "join":
                if (!levelName.equals(Main.getPlugin().getPluginConfig().getLevelName())) {
                    playerData.setPlayerGameMode(player.getGamemode());
                    playerData.setPlayerInv(player.getInventory().getContents());
                    playerData.setPlayerHunger(player.getFoodData().getLevel());
                    playerData.setBlockSecond(0);
                    playerData.setBlockMax(0);
                    player.getInventory().clearAll();
                    Utils.addItemToPlayer(player, Main.getPlugin().getPluginConfig().getBlockInfo().toItem());
                    JSONObject j = variable.configjson.getJSONObject("block").getJSONObject("pickaxe");
                    Utils.addItemToPlayer(player, Item.get(j.getInteger("id"), j.getInteger("d"), 1));
                    Position pos = Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("z")), Server.getInstance().getLevelByName(Main.getPlugin().getPluginConfig().getLevelName()));
                    playerData.setPlayerResPos(pos);
                    playerData.setPlayeronresp(false);
                    playerData.setPlayeronelevator(false);
                    playerData.setPlayerLevel(new Exp(player.getExperience(), player.getExperienceLevel()));
                    playerData.setPlayerBlock(0);
                    playerData.setPlayerTime(0);
                    JSONObject plj = JSONObject.parseObject(FileUtils.readFile("./plugins/BridgingPractise/players/" + playerName + ".json"));
                    player.setNameTag("§7[§6" + plj.getInteger("level") + "§7]§f" + player.getName());
                    if (variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("enable")) {
                        player.setExperience(plj.getInteger("exp"), plj.getInteger("level"));
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
                    player.setExperience(exp.getExp(), exp.getLv());
                    player.getFoodData().setLevel(playerData.getPlayerHunger());
                    player.setNameTag(player.getName());
                    ScoreboardUtils.removeSB(player);
                    Tips.removeTipsConfig(player.getLevel().getFolderName(), player);
                    player.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("z")), Server.getInstance().getLevelByName(variable.configjson.getJSONObject("pos").getJSONObject("exit").getString("l"))));
                    sender.sendMessage(Main.language.translateString("leavearena"));
                    PlayerData remove = Main.getPlugin().getPlayerDataMap().remove(playerName);
                    if (remove != null) {
                        remove.save();
                        remove.clear();
                    }
                } else {
                    sender.sendMessage(Main.language.translateString("notinarena"));
                }
                break;
            default:
                sender.sendMessage(Main.language.translateString("usage", variable.configjson.getJSONObject("pra").getString("command")));
        }
        return false;
    }
}