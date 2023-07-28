package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class EndListener extends Module<ListenerManager> {
    public EndListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = event.getTo().getBlock();
        if (player.getWorld().getEnvironment() == World.Environment.THE_END && block.getType().name().contains("WATER")) {
            player.teleport(this.getInstance().getWaypointManager().getEndWorldExit());
            player.sendMessage(this.getLanguageConfig().getString("END_LISTENER.ENTERED"));
        }
    }
}
