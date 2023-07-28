package dev.razorni.hcfactions.timers.listeners.servertimers;

import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.timers.Timer;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.utils.Formatter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SOTWTimer extends Timer {
    private List<UUID> enabled;
    private boolean active;
    private Long remaining;

    public SOTWTimer(TimerManager manager) {
        super(manager, "SOTW", "", 0);
        this.enabled = new ArrayList<>();
        this.remaining = 0L;
        this.active = false;
    }

    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.endSOTW();
        }
        return Formatter.formatMMSS(time);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!this.isActive()) {
            return;
        }
        Player player = event.getPlayer();
        Claim time = this.getInstance().getTeamManager().getClaimManager().getClaim(event.getTo());
        if (time != null && time.isLocked()) {
            PlayerTeam team = this.getInstance().getTeamManager().getPlayerTeam(time.getTeam());
            if (team.getPlayers().contains(player.getUniqueId())) {
                return;
            }
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!this.isActive()) {
            return;
        }
        Player player = event.getPlayer();
        Claim claim = this.getInstance().getTeamManager().getClaimManager().getClaim(event.getTo());
        if (claim != null && claim.isLocked()) {
            PlayerTeam team = this.getInstance().getTeamManager().getPlayerTeam(claim.getTeam());
            if (team == null || team.getPlayers().contains(player.getUniqueId())) {
                return;
            }
            event.setCancelled(true);
            this.getManager().getEnderpearlTimer().removeTimer(player);
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
    }

    public void extendSOTW(long time) {
        this.active = true;
        this.remaining = this.getRemaining() + time;
        for (String s : this.getLanguageConfig().getStringList("SOTW_TIMER.STARTED_SOTW")) {
            Bukkit.broadcastMessage(s.replaceAll("%time%", Formatter.formatDetailed(time)));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.active && !this.enabled.contains(player.getUniqueId())) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

    public void startSOTW(long time) {
        this.active = true;
        this.remaining = System.currentTimeMillis() + time;
        for (String s : this.getLanguageConfig().getStringList("SOTW_TIMER.STARTED_SOTW")) {
            Bukkit.broadcastMessage(s);
        }
    }

    public void endSOTW() {
        this.active = false;
        this.remaining = 0L;
        for (String s : this.getLanguageConfig().getStringList("SOTW_TIMER.ENDED_SOTW")) {
            Bukkit.broadcastMessage(s);
        }
    }
}