package net.minecraft.server;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public abstract class ScoreboardTeamBase {

    public ScoreboardTeamBase() {
    }

    public boolean isAlly(ScoreboardTeamBase scoreboardteambase) {
        return this == scoreboardteambase;
    }

    public abstract String getName();

    public abstract String getFormattedName(String s);

    public abstract boolean allowFriendlyFire();

    public abstract Collection<String> getPlayerNameSet();

    public abstract ScoreboardTeamBase.EnumNameTagVisibility j();

    public enum EnumNameTagVisibility {

        ALWAYS("always", 0), NEVER("never", 1), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2), HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, ScoreboardTeamBase.EnumNameTagVisibility> g = Maps.newHashMap();

        static {
            ScoreboardTeamBase.EnumNameTagVisibility[] ascoreboardteambase_enumnametagvisibility = values();

            for (EnumNameTagVisibility scoreboardteambase_enumnametagvisibility : ascoreboardteambase_enumnametagvisibility) {
                EnumNameTagVisibility.g.put(scoreboardteambase_enumnametagvisibility.e, scoreboardteambase_enumnametagvisibility);
            }

        }

        public final String e;
        public final int f;

        EnumNameTagVisibility(String s, int i) {
            this.e = s;
            this.f = i;
        }

        public static String[] a() {
            return EnumNameTagVisibility.g.keySet().toArray(new String[0]);
        }

        public static ScoreboardTeamBase.EnumNameTagVisibility a(String s) {
            return EnumNameTagVisibility.g.get(s);
        }
    }
}
