package cn.ricoco.bridgingpractise;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.ricoco.bridgingpractise.command.RunCommand;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.plugin.MetricsLite;
import cn.ricoco.bridgingpractise.utils.FileUtils;
import cn.ricoco.bridgingpractise.utils.LevelUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends PluginBase {

    public static Main plugin;

    private PluginConfig pluginConfig;

    @Getter
    private final ConcurrentHashMap<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public static Config languageConfig;

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
        this.pluginConfig = new PluginConfig(new Config(this.getDataFolder() + "/config.json", Config.JSON));

        String langpath = this.getDataFolder() + "/lang/" + variable.configjson.getJSONObject("pra").getString("language") + ".json";
        if (!new File(langpath).exists()) {
            plugin.getLogger().warning("LANGUAGE \"" + variable.configjson.getJSONObject("pra").getString("language") + ".json\" NOT FOUND.LOADING EN_US.json");
            langpath = this.getDataFolder() + "/lang/en_us.json";
        }
        languageConfig = new Config(langpath, Config.JSON);

        try {
            FileUtils.Copydir(this.getServer().getDataPath() + "/worlds/" + this.getPluginConfig().getLevelName() + "/", this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            this.getLogger().error("Error while copying level", e);
        }
        LevelUtils.loadLevel(this.getPluginConfig().getLevelName());

        this.getServer().getPluginManager().registerEvents(new EventLauncher(this), this);

        this.getServer().getCommandMap().register(variable.configjson.getJSONObject("pra").getString("command"), new RunCommand(variable.configjson.getJSONObject("pra").getString("command"), "Bridging Practise"));

        this.getServer().getScheduler().scheduleTask(this, new PluginTick(this), true);

        try {
            new MetricsLite(this, 8604);
        } catch (Exception ignored) {

        }

        this.getLogger().info("§eBridgingPractiseNK §fBy §bRicoGG §aSuccessfully Loaded.");
    }

    @Override
    public void onDisable() {
        String levelName = this.getPluginConfig().getLevelName();
        LevelUtils.unloadLevel(levelName);
        try {
            FileUtils.deldir(this.getServer().getDataPath() + "/worlds/" + levelName + "/");
            FileUtils.Copydir(this.getDataFolder() + "/cache/", this.getServer().getDataPath() + "/worlds/" + levelName + "/");
            FileUtils.deldir(this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            this.getLogger().error("Error while copying level", e);
        }
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    @NotNull
    public PlayerData getPlayerData(@NotNull Player player) {
        if (!playerDataMap.containsKey(player)) {
            playerDataMap.put(player, new PlayerData(player, new Config(this.getDataFolder() + "/players/" + player.getName() + ".json", Config.JSON)));
        }
        return playerDataMap.get(player);
    }
}


