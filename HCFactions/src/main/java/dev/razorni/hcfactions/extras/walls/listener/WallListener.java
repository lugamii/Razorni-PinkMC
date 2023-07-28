package dev.razorni.hcfactions.extras.walls.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.walls.WallManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class WallListener extends Module<WallManager> {

    public WallListener(WallManager wallManager) {
        super(wallManager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().clearTeamMap(player);
        this.getManager().clearWalls(player);
        this.getManager().getWalls().remove(player.getUniqueId());
        this.getManager().getTeamMaps().remove(player.getUniqueId());
    }
}
