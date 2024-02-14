package cn.ricoco.bridgingpractise;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.ricoco.bridgingpractise.Command.RunCommand;
import cn.ricoco.bridgingpractise.Plugin.MetricsLite;
import cn.ricoco.bridgingpractise.Utils.FileUtils;
import cn.ricoco.bridgingpractise.Utils.LevelUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;

public class Main extends PluginBase {

    public static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        new File(this.getDataFolder() + "/cache/").mkdirs();
        try {
            FileUtils.deldir(this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(this.getDataFolder() + "/lang/").mkdir();
        new File(this.getDataFolder() + "/players/").mkdir();
        if (!new File(this.getDataFolder() + "/config.json").exists()) {
            Level deflevel = Server.getInstance().getDefaultLevel();
            Position ws = deflevel.getSpawnLocation();
            String cfgpath = this.getDataFolder() + "/config.json";
            Position wpos = deflevel.getSafeSpawn();
            FileUtils.ReadJar("resources/config.json", cfgpath);
            FileUtils.writeFile(cfgpath, FileUtils.readFile(cfgpath).replaceAll("%1", wpos.x + "").replaceAll("%2", wpos.y + "").replaceAll("%3", wpos.z + "").replaceAll("%4", deflevel.getName()));
            LevelUtils.unzip("bpractise");
        }
        if (!new File(this.getDataFolder() + "/lang/en_us.json").exists()) {
            FileUtils.ReadJar("resources/lang/en_us.json", this.getDataFolder() + "/lang/en_us.json");
        }
        if (!new File(this.getDataFolder() + "/lang/zh_cn.json").exists()) {
            FileUtils.ReadJar("resources/lang/zh_cn.json", this.getDataFolder() + "/lang/zh_cn.json");
        }
        variable.configjson = JSONObject.parseObject(FileUtils.readFile(this.getDataFolder() + "/config.json"));
        String langpath = this.getDataFolder() + "/lang/" + variable.configjson.getJSONObject("pra").getString("language") + ".json";
        if (!new File(langpath).exists()) {
            plugin.getLogger().warning("LANGUAGE \"" + variable.configjson.getJSONObject("pra").getString("language") + ".json\" NOT FOUND.LOADING EN_US.json");
            langpath = this.getDataFolder() + "/lang/en_us.json";
        }
        variable.langjson = JSONObject.parseObject(FileUtils.readFile(langpath));
        variable.disabledmg = variable.configjson.getJSONObject("pra").getJSONArray("disabledmg");
        try {
            FileUtils.Copydir("./worlds/" + variable.configjson.getJSONObject("pos").getJSONObject("pra").getString("l") + "/", this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LevelUtils.loadLevel(variable.configjson.getJSONObject("pos").getJSONObject("pra").getString("l"));
        getServer().getPluginManager().registerEvents(new EventLauncher(this), this);
        plugin.getServer().getCommandMap().register(variable.configjson.getJSONObject("pra").getString("command"), new RunCommand(variable.configjson.getJSONObject("pra").getString("command"), "Bridging Practise"));
        variable.lowy = variable.configjson.getJSONObject("pos").getDouble("lowy");
        PluginTick.StartTick();
        try {
            new MetricsLite(this, 8604);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject placeBlock = variable.configjson.getJSONObject("block");
        variable.cantPlaceOn.add(placeBlock.getInteger("stop"));
        variable.cantPlaceOn.add(placeBlock.getInteger("res"));
        variable.cantPlaceOn.add(placeBlock.getInteger("speedup"));
        variable.cantPlaceOn.add(placeBlock.getInteger("backres"));
        variable.cantPlaceOn.add(placeBlock.getInteger("elevator"));
        Server.getInstance().getLogger().info("§eBridgingPractiseNK §fBy §bRicoGG §aSuccessfully Loaded.");
    }

    @Override
    public void onDisable() {
        LevelUtils.unloadLevel(variable.configjson.getJSONObject("pos").getJSONObject("pra").getString("l"));
        try {
            FileUtils.deldir("./worlds/" + variable.configjson.getJSONObject("pos").getJSONObject("pra").getString("l") + "/");
            FileUtils.Copydir(this.getDataFolder() + "/cache/", "./worlds/" + variable.configjson.getJSONObject("pos").getJSONObject("pra").getString("l") + "/");
            FileUtils.deldir(this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


