package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalListener extends Module<ListenerManager> {
    public PortalListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPortalCreate(EntityCreatePortalEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }
        Location from = event.getFrom();
        if (from.getWorld().getEnvironment() == World.Environment.NORMAL) {
            Location location = from.clone();
            location.setX(location.getX() / Config.NETHER_MULTIPLIER);
            location.setZ(location.getZ() / Config.NETHER_MULTIPLIER);
            location.setWorld(event.getTo().getWorld());
            event.setTo(location);
        } else if (from.getWorld().getEnvironment() == World.Environment.NETHER) {
            event.setTo(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
        }
    }

    @EventHandler
    public void onEntity(EntityPortalEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getFrom());
        if (team instanceof SafezoneTeam) {
            player.teleport(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
            player.sendMessage(this.getLanguageConfig().getString("PORTAL_LISTENER.TELEPORTED_SPAWN"));
        }
    }
}
