package cn.ricoco.bridgingpractise;

import cn.lanink.gamecore.utils.Language;
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

    public static Language language;

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
            this.getLogger().error("Error while deleting cache", e);
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

        //加载语言文件
        File langFile = new File(this.getDataFolder() + "/lang/" + this.pluginConfig.getLanguage() + ".json");
        if (!langFile.exists()) {
            plugin.getLogger().warning("LANGUAGE \"" + this.pluginConfig.getLanguage() + ".json\" NOT FOUND.LOADING EN_US.json");
            langFile = new File(this.getDataFolder() + "/lang/en_us.json");
        }
        language = new Language(new Config(langFile, Config.JSON));

        //准备地图
        try {
            FileUtils.Copydir(this.getServer().getDataPath() + "/worlds/" + this.getPluginConfig().getLevelName() + "/", this.getDataFolder() + "/cache/");
        } catch (IOException e) {
            this.getLogger().error("Error while copying level", e);
        }
        LevelUtils.loadLevel(this.getPluginConfig().getLevelName());

        this.getServer().getPluginManager().registerEvents(new EventLauncher(this), this);

        this.getServer().getCommandMap().register(this.pluginConfig.getCommand(), new RunCommand(this.pluginConfig.getCommand(), "Bridging Practise"));

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


