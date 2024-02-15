package cn.ricoco.bridgingpractise;


import cn.nukkit.utils.Config;
import lombok.Getter;

/**
 * @author LT_Name
 */
@Getter
public class PluginConfig {

    private String levelName;

    private boolean pvpProtect;


    public PluginConfig(Config config) {
        this.levelName = config.getString("pos.pra.l");

        this.pvpProtect = config.getBoolean("pra.pvpprotect");
    }

}
