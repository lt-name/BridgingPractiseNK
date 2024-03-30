package cn.ricoco.bridgingpractise.data;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.ricoco.bridgingpractise.plugin.Exp;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家数据
 *
 * @author LT_Name
 */
@Data
public class PlayerData {

    private Player player;
    private Config config;

    /**
     * 玩家放置方块位置map
     */
    private final Map<Integer, Position> blockPos = new HashMap<>();
    /**
     * 玩家放置方块数量（在PluginTick每秒清零一次）
     */
    private int blockSecond;
    private int blockMax;
    private Position playerResPos;
    /**
     * 玩家背包map
     */
    private Map<Integer, Item> playerInv;
    private int playerHunger;
    private boolean playeronresp;
    private boolean playeronelevator;
    private int playerGameMode;
    private JSONObject playerLevelJSON;
    private Exp playerLevel;
    private int playerBlock;
    private int playerTime;

    public PlayerData(@NotNull Player player, @NotNull Config config) {
        this.player = player;
        this.config = config;
    }

    public void addBlockSecond() {
        this.blockSecond++;
    }

    public void addPlayerBlock() {
        this.playerBlock++;
    }

    public void addPlayerTime() {
        this.playerTime++;
    }

    public void save() {

    }

}
