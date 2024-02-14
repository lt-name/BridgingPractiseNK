package cn.ricoco.bridgingpractise;


import cn.nukkit.utils.Config;

/**
 * @author LT_Name
 */
public class PluginConfig {

    private String levelName;


    public PluginConfig(Config config) {
        levelName = config.getString("pos.pra.l");
    }

    public String getLevelName() {
        return this.levelName;
    }
}
