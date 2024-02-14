package cn.ricoco.bridgingpractise.Command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.Plugin.ClearBlocks;
import cn.ricoco.bridgingpractise.Plugin.Exp;
import cn.ricoco.bridgingpractise.Utils.FileUtils;
import cn.ricoco.bridgingpractise.Utils.PlayerUtils;
import cn.ricoco.bridgingpractise.variable;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class RunCommand extends Command {
    public RunCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(variable.langjson.getString("notplayer"));
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(variable.langjson.getString("usage").replaceAll("%1", variable.configjson.getJSONObject("pra").getString("command")));
            return false;
        }
        Player player = (Player) sender;
        String levelName = player.getPosition().getLevel().getName();
        String playerName = player.getName();
        switch (args[0]) {
            case "join":
                if (!levelName.equals(Main.getPlugin().getPluginConfig().getLevelName())) {
                    variable.blockpos.put(playerName, new HashMap<>());
                    variable.playergamemode.put(playerName, player.getGamemode());
                    variable.playerinv.put(playerName, player.getInventory().getContents());
                    variable.playerhunger.put(playerName, player.getFoodData().getLevel());
                    variable.blocksecond.put(playerName, 0);
                    variable.blockmax.put(playerName, 0);
                    player.getInventory().clearAll();
                    JSONObject j = variable.configjson.getJSONObject("block").getJSONObject("pra");
                    PlayerUtils.addItemToPlayer(player, Item.get(j.getInteger("id"), j.getInteger("d"), j.getInteger("c")));
                    j = variable.configjson.getJSONObject("block").getJSONObject("pickaxe");
                    PlayerUtils.addItemToPlayer(player, Item.get(j.getInteger("id"), j.getInteger("d"), 1));
                    Position pos = Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("pra").getDouble("z")), Server.getInstance().getLevelByName(Main.getPlugin().getPluginConfig().getLevelName()));
                    variable.playerresp.put(playerName, pos);
                    variable.playeronresp.put(playerName, false);
                    variable.playeronelevator.put(playerName, false);
                    variable.playerLevel.put(playerName, new Exp(player.getExperience(), player.getExperienceLevel()));
                    variable.playerBlock.put(playerName, 0);
                    variable.playerTime.put(playerName, 0);
                    JSONObject plj = JSONObject.parseObject(FileUtils.readFile("./plugins/BridgingPractise/players/" + playerName + ".json"));
                    variable.playerLevelJSON.put(playerName, plj);
                    player.setNameTag("§7[§6" + plj.getInteger("level") + "§7]§f" + player.getName());
                    if (variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("enable")) {
                        player.setExperience(plj.getInteger("exp"), plj.getInteger("level"));
                    } else {
                        player.setExperience(0);
                    }
                    sender.sendMessage(variable.langjson.getString("joinedarena"));
                    player.teleport(pos);
                    player.setGamemode(0);
                } else {
                    sender.sendMessage(variable.langjson.getString("stillinarena"));
                }
                break;
            case "leave":
                if (levelName.equals(Main.getPlugin().getPluginConfig().getLevelName())) {
                    ClearBlocks.clearBlocks(variable.blockpos.remove(player.getName()), true);
                    player.setGamemode(variable.playergamemode.get(playerName));
                    player.getInventory().setContents(variable.playerinv.remove(playerName));
                    variable.playerresp.remove(playerName);
                    variable.blocksecond.remove(playerName);
                    variable.blockmax.remove(playerName);
                    variable.playeronresp.remove(playerName);
                    variable.playeronelevator.remove(playerName);
                    variable.playerBlock.remove(playerName);
                    variable.playerTime.remove(playerName);
                    Exp exp = variable.playerLevel.remove(playerName);
                    player.setExperience(exp.getExp(), exp.getLv());
                    FileUtils.writeFile("./plugins/BridgingPractise/players/" + playerName + ".json", JSONObject.toJSONString(variable.playerLevelJSON.remove(playerName)));
                    player.getFoodData().setLevel(variable.playerhunger.remove(playerName));
                    player.setNameTag(player.getName());
                    player.teleport(Position.fromObject(new Vector3(variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("x"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("y"), variable.configjson.getJSONObject("pos").getJSONObject("exit").getDouble("z")), Server.getInstance().getLevelByName(variable.configjson.getJSONObject("pos").getJSONObject("exit").getString("l"))));
                    sender.sendMessage(variable.langjson.getString("leavearena"));
                } else {
                    sender.sendMessage(variable.langjson.getString("notinarena"));
                }
                break;
            default:
                sender.sendMessage(variable.langjson.getString("usage").replaceAll("%1", variable.configjson.getJSONObject("pra").getString("command")));
        }
        return false;
    }
}