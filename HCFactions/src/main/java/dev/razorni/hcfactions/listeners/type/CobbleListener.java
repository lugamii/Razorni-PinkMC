package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CobbleListener extends Module<ListenerManager> {
    public CobbleListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (event.getItem().getItemStack().getType() != Material.COBBLESTONE) {
            return;
        }
        if (!user.isCobblePickup()) {
            event.setCancelled(true);
        }
    }
}
