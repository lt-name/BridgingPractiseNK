package cn.ricoco.bridgingpractise;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.PluginTask;
import cn.ricoco.bridgingpractise.Utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class PluginTick extends PluginTask<Main> {

    private int tick = 0;

    public PluginTick(Main owner) {
        super(owner);
    }

    @Override
    public void onRun(int t) {
        String promptstr = variable.langjson.getString("prompt");
        String levelName = this.owner.getPluginConfig().getLevelName();
        String weatherstr = variable.configjson.getJSONObject("pra").getString("weather");
        int ltime = variable.configjson.getJSONObject("pra").getInteger("time");
        Boolean prompt = variable.configjson.getJSONObject("pra").getBoolean("prompt");
        Boolean expSystem = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("enable");
        Boolean lvUp = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("levelup");
        Boolean expTip = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("getexp");
        Boolean scoreb = variable.configjson.getJSONObject("pra").getJSONObject("exp").getBoolean("scoreboard");
        int SBCount = 0;
        String sbTitle = variable.langjson.getString("sbtitle");
        String[] sbTitleL = variable.langjson.getString("sbtitle").split("");
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
                        if (tick >= 1) {
                            arenac++;
                            p.getFoodData().setLevel(20);
                            if (expSystem) {
                                if (!variable.playerLevelJSON.containsKey(p.getName())) {
                                    continue;
                                }
                                JSONObject plj = variable.playerLevelJSON.get(p.getName());
                                if (timeEarn) {
                                    variable.playerTime.put(p.getName(), variable.playerTime.getOrDefault(p.getName(), 0) + 1);
                                    if (variable.playerTime.get(p.getName()) >= timeEarnC) {
                                        variable.playerTime.put(p.getName(), 0);
                                        ExpUtils.addExp(plj, timeEarnE, expTip, lvUp, "timeearn", p);
                                        variable.playerLevelJSON.put(p.getName(), plj);
                                    }
                                }
                                if (blockEarn) {
                                    int pBC = variable.playerBlock.getOrDefault(p.getName(), 0);
                                    if (pBC >= blockEarnC) {
                                        variable.playerBlock.put(p.getName(), pBC % blockEarnC);
                                        int addExp = pBC / blockEarnC;
                                        ExpUtils.addExp(plj, blockEarnE * addExp, expTip, lvUp, "blockearn", p);
                                        variable.playerLevelJSON.put(p.getName(), plj);
                                    }
                                }
                                p.setExperience(plj.getInteger("exp"), plj.getInteger("level"));
                            } else {
                                p.setExperience(0);
                            }
                            if (prompt) {
                                p.sendPopup(promptstr.replaceAll("%1", variable.blocksecond.get(p.getName()) + "").replaceAll("%2", variable.blockpos.get(p.getName()).size() + "").replaceAll("%3", variable.blockmax.get(p.getName()) + ""));
                            }
                            variable.blocksecond.put(p.getName(), 0);
                        }
                        if (scoreb) {
                            ArrayList<String> arr = new ArrayList<>();
                            JSONObject plj = variable.playerLevelJSON.get(p.getName());
                            String SB_Player = p.getName(), SB_Level = plj.getInteger("level") + "", SB_LowProgcess = plj.getInteger("exp") + "", SB_MaxProgcess = ExpUtils.calcNeedExp(plj.getInteger("level") + 1) + "", SB_Placed = plj.getInteger("place") + "";
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
