package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpawnTimer extends PlayerTimer {
    public SpawnTimer(TimerManager manager) {
        super(manager, false, "Spawn", "PLAYER_TIMERS.SPAWN", manager.getConfig().getInt("SPAWN_TIMER.TIME"));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = Utils.getDamager(event.getDamager());
        Player damaged = (Player) event.getEntity();
        if (damager == null) {
            return;
        }
        if (damager == damaged) {
            return;
        }
        if (!this.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            return;
        }
        if (this.hasTimer(damaged)) {
            this.removeTimer(damaged);
            damaged.sendMessage(this.getLanguageConfig().getString("SPAWN_TIMER.DAMAGED"));
        }
        if (this.hasTimer(damager)) {
            this.removeTimer(damager);
            damager.sendMessage(this.getLanguageConfig().getString("SPAWN_TIMER.DAMAGED"));
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getTimer() instanceof SpawnTimer)) {
            return;
        }
        Player player = Bukkit.getPlayer(event.getPlayer());
        Location spawn = Bukkit.getWorld("world").getSpawnLocation();
        if (player != null) {
            Tasks.execute(this.getManager(), () -> player.teleport(spawn.add(0.5, 0.0, 0.5)));
            player.sendMessage(this.getLanguageConfig().getString("SPAWN_TIMER.WARPED"));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(this.getLanguageConfig().getString("SPAWN_TIMER.MOVED"));
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
            player.sendMessage(this.getLanguageConfig().getString("SPAWN_TIMER.MOVED"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeTimer(event.getPlayer());
    }
}