package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderpearlTimer extends PlayerTimer {
    public EnderpearlTimer(TimerManager manager) {
        super(manager, false, "Enderpearl", "PLAYER_TIMERS.ENDER_PEARL", manager.getConfig().getInt("TIMERS_COOLDOWN.ENDER_PEARL"));
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity().getShooter();
        if (!this.hasTimer(shooter)) {
            this.applyTimer(shooter);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (!event.getItem().getType().equals(Material.ENDER_PEARL)) {
            return;
        }
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(this.getLanguageConfig().getString("ENDERPEARL_TIMER.COOLDOWN").replaceAll("%seconds%", this.getRemainingString(player)));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.hasTimer(player)) {
            event.setCancelled(true);
        }
    }
}
