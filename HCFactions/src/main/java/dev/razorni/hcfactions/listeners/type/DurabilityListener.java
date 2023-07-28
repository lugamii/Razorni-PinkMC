package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class DurabilityListener extends Module<ListenerManager> {
    private final int chance;

    public DurabilityListener(ListenerManager manager) {
        super(manager);
        this.chance = this.getConfig().getInt("DURABILITY_FIX.PERCENT");
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        if (!this.getConfig().getBoolean("DURABILITY_FIX.ENABLED")) {
            return;
        }
        int i = ThreadLocalRandom.current().nextInt(101);
        if (i <= this.chance) {
            event.setCancelled(true);
        }
    }
}
