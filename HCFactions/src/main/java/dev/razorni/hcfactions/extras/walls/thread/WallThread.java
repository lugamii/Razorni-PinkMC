package dev.razorni.hcfactions.extras.walls.thread;

import dev.razorni.hcfactions.extras.walls.WallManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.timers.listeners.playertimers.InvincibilityTimer;
import dev.razorni.hcfactions.timers.listeners.playertimers.PvPTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WallThread extends Thread {

    private final WallManager manager;

    public WallThread(WallManager wallManager) {
        super("Azurite - WallThread");
        this.manager = wallManager;
        this.start();
    }

    private void tick(Player player) {
        if (!player.getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return;
        }
        this.manager.clearWalls(player);
        if (this.manager.getInstance().getTimerManager().getSotwTimer().isActive()) {
            for (Claim claim : this.manager.getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), 5)) {
                Team team = this.manager.getInstance().getTeamManager().getTeam(claim.getTeam());
                if (!(team instanceof PlayerTeam)) {
                    continue;
                }
                PlayerTeam playerteam = (PlayerTeam) team;
                if (!claim.isLocked() || playerteam.getPlayers().contains(player.getUniqueId())) {
                    continue;
                }
                this.manager.sendWall(player, claim, true);
            }
        }
        if (this.manager.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            for (Claim claim : this.manager.getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), 5)) {
                Team team = this.manager.getInstance().getTeamManager().getTeam(claim.getTeam());
                if (team instanceof SafezoneTeam) {
                    this.manager.sendWall(player, claim, false);
                }
            }
            return;
        }
        PvPTimer pvptimer = this.manager.getInstance().getTimerManager().getPvpTimer();
        if (pvptimer.hasTimer(player)) {
            for (Claim claim : this.manager.getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), 5)) {
                Team team = this.manager.getInstance().getTeamManager().getTeam(claim.getTeam());
                if (pvptimer.checkEntry(player, team)) {
                    this.manager.sendWall(player, claim, false);
                }
            }
            return;
        }
        InvincibilityTimer invincibilityTimer = this.manager.getInstance().getTimerManager().getInvincibilityTimer();
        if (invincibilityTimer.hasTimer(player)) {
            for (Claim claim : this.manager.getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), 5)) {
                Team team = this.manager.getInstance().getTeamManager().getTeam(claim.getTeam());
                if (invincibilityTimer.checkEntry(player, team)) {
                    this.manager.sendWall(player, claim, false);
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    this.tick(player);
                } catch (Exception ignored) {
                }
            }
            try {
                sleep(150L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
