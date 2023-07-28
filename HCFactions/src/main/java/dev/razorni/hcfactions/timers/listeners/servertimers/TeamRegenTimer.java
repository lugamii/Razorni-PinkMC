package dev.razorni.hcfactions.timers.listeners.servertimers;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.timers.Timer;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamRegenTimer extends Timer {
    private final Map<PlayerTeam, Long> teamsRegenerating;

    public TeamRegenTimer(TimerManager manager) {
        super(manager, "RegenTimer", "", manager.getTeamConfig().getInt("TEAM_DTR.REGEN_TIMER") * 60);
        this.teamsRegenerating = new ConcurrentHashMap<>();
        Tasks.executeScheduledAsync(this.getManager(), 20, this::tick);
    }

    public void applyTimer(PlayerTeam team) {
            this.teamsRegenerating.put(team, System.currentTimeMillis() + this.seconds * 1000L);
            team.setMinuteRegen(false);
    }

    public void applyTimer(PlayerTeam team, long time) {
            this.teamsRegenerating.put(team, System.currentTimeMillis() + time);
            team.setMinuteRegen(false);

    }

    public void startMinuteRegen(PlayerTeam team) {
        if (team.isMinuteRegen()) {
            new MinuteRegenTask(this.instance, team);
        }
    }

    public long getRemaining(PlayerTeam team) {
        return this.teamsRegenerating.containsKey(team) ? (this.teamsRegenerating.get(team) - System.currentTimeMillis()) : 0L;
    }

    private void tick() {
        Iterator<PlayerTeam> teams = this.teamsRegenerating.keySet().iterator();
        TeamManager manager = this.getInstance().getTeamManager();
        while (teams.hasNext()) {
            PlayerTeam team = teams.next();
            if (!manager.getTeams().containsKey(team.getUniqueID())) {
                teams.remove();
            } else {
                if (this.hasTimer(team)) {
                    continue;
                }
                teams.remove();
                new MinuteRegenTask(this.instance, team);
            }
        }
    }

    public boolean hasTimer(PlayerTeam team) {
        return this.getRemaining(team) > 0L;
    }

    private static class MinuteRegenTask extends BukkitRunnable {
        private final PlayerTeam pt;
        private final HCF instance;

        public MinuteRegenTask(HCF plugin, PlayerTeam team) {
            this.instance = plugin;
            this.pt = team;
            this.runTaskTimer(plugin, 0L, 1200L);
        }

        public void run() {
            if (!this.instance.getTeamManager().getTeams().containsKey(this.pt.getUniqueID())) {
                this.cancel();
                return;
            }
            if (this.instance.getTimerManager().getTeamRegenTimer().hasTimer(this.pt)) {
                this.cancel();
                this.pt.setMinuteRegen(false);
                return;
            }
            this.pt.setMinuteRegen(true);
            this.pt.setDtr(this.pt.getDtr() + Config.DTR_REGEN_PER_MIN);
            this.pt.broadcast(this.instance.getTimerManager().getLanguageConfig().getString("TEAM_REGEN_TIMER.REGENERATING").replaceAll("%dtr%", String.valueOf(Config.DTR_REGEN_PER_MIN)));
            if (this.pt.getDtr() == this.pt.getMaxDtr()) {
                this.cancel();
                this.pt.setMinuteRegen(false);
                this.pt.broadcast(this.instance.getTimerManager().getLanguageConfig().getString("TEAM_REGEN_TIMER.FINISHED_REGENERATING"));
            }
        }
    }
}
