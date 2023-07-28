package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class LogoutTimer extends PlayerTimer {

    public LogoutTimer(TimerManager manager) {
        super(manager, false, "Logout", "PLAYER_TIMERS.LOGOUT", manager.getConfig().getInt("TIMERS_COOLDOWN.LOGOUT"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getTimer() instanceof LogoutTimer)) {
            return;
        }
        Player player = Bukkit.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }
        player.setMetadata("loggedout", new FixedMetadataValue(this.instance, true));
        player.kickPlayer(this.getLanguageConfig().getString("LOGOUT_COMMAND.LOGGED_OUT"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = (Player) event.getEntity();
        Player damager = Utils.getDamager(event.getDamager());
        if (damager == null) {
            return;
        }
        if (this.hasTimer(damaged) && this.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            this.removeTimer(damaged);
            damaged.sendMessage(this.getLanguageConfig().getString("LOGOUT_COMMAND.DAMAGED_CANCELLED"));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(this.getLanguageConfig().getString("LOGOUT_COMMAND.MOVED_CANCELLED"));
        }
    }
}