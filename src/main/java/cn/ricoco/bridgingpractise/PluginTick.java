package cn.ricoco.bridgingpractise;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.PluginTask;
import cn.ricoco.bridgingpractise.data.PlayerData;
import cn.ricoco.bridgingpractise.utils.ExpUtils;
import cn.ricoco.bridgingpractise.utils.LevelUtils;
import cn.ricoco.bridgingpractise.utils.ScoreboardUtils;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

public class PluginTick extends PluginTask<Main> {

    private int tick = 0;

    public PluginTick(Main owner) {
        super(owner);
    }

    @Override
    public void onRun(int t) {
        String promptstr = Main.language.translateString("prompt");
        String levelName = this.owner.getPluginConfig().getLevelName();
        String weatherstr = variable.configjson.getJSONObject("pra").getString("weather");
        int ltime = variable.configjson.getJSONObject("pra").getInteger("time");
        Boolean prompt = variable.configjson.getJSONObject("pra").getBoolean("prompt");
        Boolean expSystem = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("enable");
        Boolean lvUp = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("levelup");
        Boolean expTip = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("getexp");
        Boolean scoreb = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("scoreboard");
        int SBCount = 0;
        String sbTitle = Main.language.translateString("sbtitle");
        String[] sbTitleL = Main.language.translateString("sbtitle").split("");
        JSONArray SBThing = variable.configjson.getJSONObject("pra").getJSONArray("scoreboard");
        Boolean timeEarn = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("timeearn").getBoolean("enable");
        int timeEarnC = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("timeearn").getInteger("sec");
        int timeEarnE = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("timeearn").getInteger("exp");
        Boolean blockEarn = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("blockearn").getBoolean("enable");
        int blockEarnC = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("blockearn").getInteger("bls");
        int blockEarnE = variable.configjson.getJSONObject("pra").getJSONObject("exp").getJSONObject("blockearn").getInteger("exp");

        while (true) {
            try {
                Thread.sleep(500);
                tick++;
                Collection<Player> players = Server.getInstance().getOnlinePlayers().values();
                if (players.isEmpty()) {
                    continue;
                }
                int arenac = 0;
                String SB_Title = "";
                if (SBCount < sbTitleL.length) {
                    for (int i = 0; i < sbTitleL.length; i++) {
                        if (i < SBCount) {
                            SB_Title = SB_Title + "§f" + sbTitleL[i];
                        } else {
                            SB_Title = SB_Title + "§e" + sbTitleL[i];
                        }
                    }
                } else if (SBCount == sbTitleL.length || SBCount == sbTitleL.length + 2) {
                    SB_Title = "§f" + sbTitle;
                } else if (SBCount == sbTitleL.length + 1) {
                    SB_Title = "§e" + sbTitle;
                } else if (SBCount == sbTitleL.length + 3) {
                    SB_Title = "§e" + sbTitle;
                    SBCount = 0;
                }
                SBCount++;
                for (Player p : players) {
                    if (p.getLevel().getName().equals(levelName)) {
                        PlayerData playerData = this.owner.getPlayerData(p);
                        if (tick >= 1) {
                            arenac++;
                            p.getFoodData().setLevel(20);
                            if (expSystem) {
                                if (timeEarn) {
                                    playerData.addPlayerTime();
                                    if (playerData.getPlayerTime() >= timeEarnC) {
                                        playerData.setPlayerTime(0);
                                        ExpUtils.addExp(playerData, timeEarnE, expTip, lvUp, "timeearn", p);
                                    }
                                }
                                if (blockEarn) {
                                    int pBC = playerData.getPlayerBlock();
                                    if (pBC >= blockEarnC) {
                                        playerData.setPlayerBlock(pBC % blockEarnC);
                                        int addExp = pBC / blockEarnC;
                                        ExpUtils.addExp(playerData, blockEarnE * addExp, expTip, lvUp, "blockearn", p);
                                    }
                                }
                                p.setExperience(playerData.getExp(), playerData.getLevel());
                            } else {
                                p.setExperience(0);
                            }
                            if (prompt) {
                                p.sendPopup(promptstr.replaceAll("%1", String.valueOf(playerData.getBlockSecond())).replaceAll("%2", playerData.getBlockPos().size() + "").replaceAll("%3", String.valueOf(playerData.getBlockMax())));
                            }
                            playerData.setBlockSecond(0);
                        }
                        if (scoreb) {
                            ArrayList<String> arr = new ArrayList<>();
                            String SB_Player = p.getName(), SB_Level = playerData.getLevel() + "", SB_LowProgcess = playerData.getExp() + "", SB_MaxProgcess = ExpUtils.calcNeedExp(playerData.getLevel() + 1) + "", SB_Placed = playerData.getPlace() + "";
                            p.setNameTag("§7[e" + SB_Level + "§7]§f" + p.getName());
                            for (int i = 0; i < SBThing.size(); i++) {
                                arr.add(SBThing.getString(i).replaceAll("%player", SB_Player).replaceAll("%level%", SB_Level).replaceAll("%lowProgcess%", SB_LowProgcess).replaceAll("%maxProgcess%", SB_MaxProgcess).replaceAll("%placed%", SB_Placed));
                            }
                            ScoreboardUtils.showSBFromArrayList(p, arr, SB_Title);
                        }
                    }
                }
                if (arenac > 0 && tick >= 1) {
                    Level level = Server.getInstance().getLevelByName(levelName);
                    level.setTime(ltime);
                    LevelUtils.setLevelWeather(level, weatherstr);
                }
            } catch (Exception e) {
                Main.getPlugin().getLogger().error("Error in PluginTick: ", e);
            }
        }
    }
}
