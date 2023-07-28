package net.minecraft.server;

import eu.vortexdev.invictusspigot.InvictusSpigot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class PersistentScoreboard extends PersistentBase {

    private static final Logger b = LogManager.getLogger();
    private Scoreboard c;
    private NBTTagCompound d;

    public PersistentScoreboard() {
        this("scoreboard");
    }

    public PersistentScoreboard(String s) {
        super(s);
    }

    public void a(Scoreboard scoreboard) {
        this.c = scoreboard;
        if (this.d != null) {
            this.a(this.d);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        if (this.c == null) {
            this.d = nbttagcompound;
        } else {
            this.b(nbttagcompound.getList("Objectives", 10));
            this.c(nbttagcompound.getList("PlayerScores", 10));
            if (nbttagcompound.hasKeyOfType("DisplaySlots", 10)) {
                this.c(nbttagcompound.getCompound("DisplaySlots"));
            }

            if (nbttagcompound.hasKeyOfType("Teams", 9)) {
                this.a(nbttagcompound.getList("Teams", 10));
            }

        }
    }

    protected void a(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            String s = nbttagcompound.getString("Name");
            if (s.length() > 16) {
                s = s.substring(0, 16);
            }

            ScoreboardTeam scoreboardteam = this.c.createTeam(s);
            String s1 = nbttagcompound.getString("DisplayName");

            if (s1.length() > 32) {
                s1 = s1.substring(0, 32);
            }

            scoreboardteam.setDisplayName(s1);
            if (nbttagcompound.hasKeyOfType("TeamColor", 8)) {
                scoreboardteam.a(EnumChatFormat.b(nbttagcompound.getString("TeamColor")));
            }

            scoreboardteam.setPrefix(nbttagcompound.getString("Prefix"));
            scoreboardteam.setSuffix(nbttagcompound.getString("Suffix"));
            if (nbttagcompound.hasKeyOfType("AllowFriendlyFire", 99)) {
                scoreboardteam.setAllowFriendlyFire(nbttagcompound.getBoolean("AllowFriendlyFire"));
            }

            if (nbttagcompound.hasKeyOfType("SeeFriendlyInvisibles", 99)) {
                scoreboardteam.setCanSeeFriendlyInvisibles(nbttagcompound.getBoolean("SeeFriendlyInvisibles"));
            }

            ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility;

            if (nbttagcompound.hasKeyOfType("NameTagVisibility", 8)) {
                scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(nbttagcompound.getString("NameTagVisibility"));
                if (scoreboardteambase_enumnametagvisibility != null) {
                    scoreboardteam.setNameTagVisibility(scoreboardteambase_enumnametagvisibility);
                }
            }

            if (nbttagcompound.hasKeyOfType("DeathMessageVisibility", 8)) {
                scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(nbttagcompound.getString("DeathMessageVisibility"));
                if (scoreboardteambase_enumnametagvisibility != null) {
                    scoreboardteam.b(scoreboardteambase_enumnametagvisibility);
                }
            }

            this.a(scoreboardteam, nbttagcompound.getList("Players", 8));
        }

    }

    protected void a(ScoreboardTeam scoreboardteam, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.c.addPlayerToTeam(nbttaglist.getString(i), scoreboardteam.getName());
        }

    }

    protected void c(NBTTagCompound nbttagcompound) {
        for (int i = 0; i < 19; ++i) {
            if (nbttagcompound.hasKeyOfType("slot_" + i, 8)) {
                this.c.setDisplaySlot(i, c.getObjective(nbttagcompound.getString("slot_" + i)));
            }
        }

    }

    protected void b(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            IScoreboardCriteria iscoreboardcriteria = IScoreboardCriteria.criteria.get(nbttagcompound.getString("CriteriaName"));

            if (iscoreboardcriteria != null) {
                String s = nbttagcompound.getString("Name");
                if (s.length() > 16) {
                    s = s.substring(0, 16);
                }

                ScoreboardObjective scoreboardobjective = this.c.registerObjective(s, iscoreboardcriteria);

                scoreboardobjective.setDisplayName(nbttagcompound.getString("DisplayName"));
                scoreboardobjective.a(IScoreboardCriteria.EnumScoreboardHealthDisplay.a(nbttagcompound.getString("RenderType")));
            }
        }

    }

    protected void c(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            ScoreboardObjective scoreboardobjective = this.c.getObjective(nbttagcompound.getString("Objective"));
            String s = nbttagcompound.getString("Name");
            if (s.length() > 40) {
                s = s.substring(0, 40);
            }

            ScoreboardScore scoreboardscore = this.c.getPlayerScoreForObjective(s, scoreboardobjective);

            scoreboardscore.setScore(nbttagcompound.getInt("Score"));
            if (nbttagcompound.hasKey("Locked")) {
                scoreboardscore.a(nbttagcompound.getBoolean("Locked"));
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        InvictusSpigot.INSTANCE.getThreadingManager().execute(() -> {
    		if (c == null) {
    			b.warn("Tried to save scoreboard without having a scoreboard...");
    		} else {
    			nbttagcompound.set("Objectives", b());
    			nbttagcompound.set("PlayerScores", e());
    			nbttagcompound.set("Teams", a());
    			d(nbttagcompound);
    		}
    	});
    }

    protected NBTTagList a() {
        NBTTagList nbttaglist = new NBTTagList();
        Collection<ScoreboardTeam> collection = this.c.getTeams();

        for (ScoreboardTeam scoreboardteam : collection) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("Name", scoreboardteam.getName());
            nbttagcompound.setString("DisplayName", scoreboardteam.getDisplayName());
            if (scoreboardteam.l().b() >= 0) {
                nbttagcompound.setString("TeamColor", scoreboardteam.l().e());
            }

            nbttagcompound.setString("Prefix", scoreboardteam.getPrefix());
            nbttagcompound.setString("Suffix", scoreboardteam.getSuffix());
            nbttagcompound.setBoolean("AllowFriendlyFire", scoreboardteam.allowFriendlyFire());
            nbttagcompound.setBoolean("SeeFriendlyInvisibles", scoreboardteam.canSeeFriendlyInvisibles());
            nbttagcompound.setString("NameTagVisibility", scoreboardteam.getNameTagVisibility().e);
            nbttagcompound.setString("DeathMessageVisibility", scoreboardteam.j().e);
            NBTTagList nbttaglist1 = new NBTTagList();

            for (String s : scoreboardteam.getPlayerNameSet()) {
                nbttaglist1.add(new NBTTagString(s));
            }

            nbttagcompound.set("Players", nbttaglist1);
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    protected void d(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        boolean flag = false;

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = this.c.getObjectiveForSlot(i);

            if (scoreboardobjective != null) {
                nbttagcompound1.setString("slot_" + i, scoreboardobjective.getName());
                flag = true;
            }
        }

        if (flag) {
            nbttagcompound.set("DisplaySlots", nbttagcompound1);
        }

    }

    protected NBTTagList b() {
        NBTTagList nbttaglist = new NBTTagList();
        Collection<ScoreboardObjective> collection = this.c.getObjectives();

        for (ScoreboardObjective scoreboardobjective : collection) {
            if (scoreboardobjective.getCriteria() != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setString("Name", scoreboardobjective.getName());
                nbttagcompound.setString("CriteriaName", scoreboardobjective.getCriteria().getName());
                nbttagcompound.setString("DisplayName", scoreboardobjective.getDisplayName());
                nbttagcompound.setString("RenderType", scoreboardobjective.e().a());
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    protected NBTTagList e() {
        NBTTagList nbttaglist = new NBTTagList();
        Collection<ScoreboardScore> collection = this.c.getScores();

        for (ScoreboardScore scoreboardscore : collection) {
            if (scoreboardscore.getObjective() != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setString("Name", scoreboardscore.getPlayerName());
                nbttagcompound.setString("Objective", scoreboardscore.getObjective().getName());
                nbttagcompound.setInt("Score", scoreboardscore.getScore());
                nbttagcompound.setBoolean("Locked", scoreboardscore.g());
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }
}
