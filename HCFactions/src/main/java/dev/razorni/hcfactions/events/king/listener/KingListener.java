package dev.razorni.hcfactions.events.king.listener;

import dev.razorni.hcfactions.events.king.KingManager;
import dev.razorni.hcfactions.extras.framework.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KingListener extends Module<KingManager> {
    public KingListener(KingManager manager) {
        super(manager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().getKing() == player) {
            this.getManager().stopKing(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.getManager().getKing() == player) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            this.getManager().stopKing(false);
            this.getInstance().getNametagManager().update();
        }
    }
}
