package dev.razorni.crates.crate.effect;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import org.bukkit.Bukkit;

public class CrateEffectManager {

    public final Crates plugin;

    public CrateEffectManager(Crates plugin) {
        this.plugin = plugin;

        doTick();
    }

    public void doTick() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            for (Crate crate : plugin.getCrateManager().getCrateMap().values()) {
                if (crate.getEffect() == null)
                    continue;

                if (crate.getLocation() == null)
                    continue;

                crate.getEffect().tick(crate.getLocation());
            }

        }, 1L, 40L);

    }
}
