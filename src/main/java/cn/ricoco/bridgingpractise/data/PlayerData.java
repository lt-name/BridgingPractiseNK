package cn.ricoco.bridgingpractise.data;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.ricoco.bridgingpractise.plugin.Exp;
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
    private Exp playerLevel;
    private int playerBlock;
    private int playerTime;

    private int level;
    private int place;
    private int exp;


    public PlayerData(@NotNull Player player, @NotNull Config config) {
        this.player = player;
        this.config = config;

        this.level = config.getInt("level");
        this.place = config.getInt("place");
        this.exp = config.getInt("exp");
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

    public void addPlace(int count) {
        this.place += count;
    }

    public void save() {
        this.config.set("level", this.level);
        this.config.set("place", this.place);
        this.config.set("exp", this.exp);
        this.config.save();
    }

    public void clear() {
        this.blockPos.clear();
        this.blockSecond = 0;
        if (this.playerInv != null) {
            this.playerInv.clear();
        }
        this.playerHunger = 20;
        this.playeronresp = false;
        this.playeronelevator = false;
        this.playerGameMode = 0;
        this.playerBlock = 0;
        this.playerTime = 0;

        this.level = 0;
        this.place = 0;
        this.exp = 0;
    }

}
