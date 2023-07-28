package dev.razorni.hcfactions.events.koth.listener;

import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.events.koth.KothManager;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.staff.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class KothListener extends Module<KothManager> {
    public KothListener(KothManager manager) {
        super(manager);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        this.checkMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        this.checkMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Location location = event.getPlayer().getLocation();
        this.checkMove(event.getPlayer(), location, location);
    }

    private void checkMove(Player player, Location pos1, Location pos2) {
        Koth koth1 = this.getManager().getZone(pos1);
        Koth koth2 = this.getManager().getZone(pos2);
        if (koth1 != null && koth2 == null) {
            if (koth1.getCapturing() == player) {
                koth1.setCapturing(null);
                if (koth1.isActive() && koth1.getRemaining() <= koth1.getMinutes() - 30000L) {
                    for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.BROADCAST_LOST")) {
                        Bukkit.broadcastMessage(s.replaceAll("%koth%", koth1.getName()).replaceAll("%player%", player.getName()));
                    }
                }
            }
            koth1.getOnCap().remove(player);
            return;
        }
        if (koth2 != null) {
            StaffManager manager = this.getInstance().getStaffManager();
            if (manager.isStaffEnabled(player)) {
                return;
            }
            if (manager.isVanished(player)) {
                return;
            }
            if (koth2.getCapturing() == null) {
                koth2.setCapturing(player);
                if (koth2.isActive()) {
                    for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.PLAYER_CONTROLLING")) {
                        player.sendMessage(s.replaceAll("%koth%", koth2.getName()));
                    }
                }
            }
            if (!koth2.getOnCap().contains(player)) {
                koth2.getOnCap().add(player);
            }
        }
    }
}
