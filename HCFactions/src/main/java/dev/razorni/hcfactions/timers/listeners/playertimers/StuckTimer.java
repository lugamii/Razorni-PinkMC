package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StuckTimer extends PlayerTimer {
    private final Map<UUID, Location> locations;
    private final int maxMoveBlocks;

    public StuckTimer(TimerManager manager) {
        super(manager, false, "Stuck", "PLAYER_TIMERS.STUCK", manager.getConfig().getInt("TIMERS_COOLDOWN.STUCK"));
        this.locations = new HashMap<>();
        this.maxMoveBlocks = this.getTeamConfig().getInt("TEAMS.F_STUCK_MAX_MOVE");
    }

    public int getMaxMoveBlocks() {
        return this.maxMoveBlocks;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            this.check(player, event.getTo());
        }
    }

    @Override
    public void applyTimer(Player player, long time) {
        this.locations.put(player.getUniqueId(), player.getLocation());
        super.applyTimer(player, time);
    }

    public Map<UUID, Location> getLocations() {
        return this.locations;
    }

    @Override
    public void applyTimer(Player player) {
        this.locations.put(player.getUniqueId(), player.getLocation());
        super.applyTimer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeTimer(event.getPlayer());
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getTimer() != this) {
            return;
        }
        Player player = Bukkit.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }
        Tasks.execute(this.getManager(), () -> this.getInstance().getTeamManager().getClaimManager().teleportSafe(player));
        player.sendMessage(this.getLanguageConfig().getString("STUCK_TIMER.TELEPORTED"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(this.getLanguageConfig().getString("STUCK_TIMER.DAMAGED"));
        }
    }

    @Override
    public void removeTimer(Player player) {
        this.locations.remove(player.getUniqueId());
        super.removeTimer(player);
    }

    private void check(Player player, Location location) {
        if (!this.hasTimer(player)) {
            return;
        }
        Location playerLocation = this.locations.get(player.getUniqueId());
        int x = Math.abs(playerLocation.getBlockX() - location.getBlockX());
        int y = Math.abs(playerLocation.getBlockY() - location.getBlockY());
        int z = Math.abs(playerLocation.getBlockZ() - location.getBlockZ());
        if (x > this.maxMoveBlocks || y > this.maxMoveBlocks || z > this.maxMoveBlocks) {
            this.removeTimer(player);
            player.sendMessage(this.getLanguageConfig().getString("STUCK_TIMER.MOVED").replaceAll("%amount%", String.valueOf(this.maxMoveBlocks)));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            this.check(player, event.getTo());
        }
    }
}
