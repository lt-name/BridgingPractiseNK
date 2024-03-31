package cn.ricoco.bridgingpractise.utils;

import cn.nukkit.Player;
import cn.ricoco.bridgingpractise.Main;
import cn.ricoco.bridgingpractise.data.PlayerData;

public class ExpUtils {
    public static int calcExp(int lv) {
        if (lv <= 16) {
            //[Level]2 + 6[Level]
            return (int) Math.round(Math.pow(lv, 2) + (6 * lv));
        } else if (lv <= 31) {
            //2.5[Level]2 - 40.5[Level] + 360
            return (int) Math.round((2.5 * Math.pow(lv, 2)) - (40.5 * lv) + 360);
        } else {
            //4.5[Level]2 - 162.5[Level] + 2220
            return (int) Math.round((4.5 * Math.pow(lv, 2)) - (162.5 * lv) + 2220);
        }
    }

    public static int calcNeedExp(int lv) {
        if (lv <= 16) {
            //2[Current Level] + 7
            return (int) Math.round((2 * lv) + 7);
        } else if (lv <= 31) {
            //5[Current Level] - 38
            return (int) Math.round((5 * lv) - 38);
        } else {
            //9[Current Level] - 158
            return (int) Math.round((9 * lv) - 158);
        }
    }

    public static PlayerData addExp(PlayerData playerData, int add, Boolean expTip, Boolean lvUp, String earn, Player p) {
        int need = calcNeedExp(playerData.getLevel() + 1);
        if (expTip) {
            p.sendMessage(Main.language.translateString(earn, add));
        }
        if (need < (playerData.getExp() + add)) {
            playerData.setLevel(playerData.getLevel() + 1);
            playerData.setExp((playerData.getExp() + add) - need);
            if (lvUp) {
                p.sendMessage(Main.language.translateString("levelup", playerData.getLevel()));
            }
        } else {
            playerData.setExp(playerData.getExp() + add);
        }
        return playerData;
    }
}
