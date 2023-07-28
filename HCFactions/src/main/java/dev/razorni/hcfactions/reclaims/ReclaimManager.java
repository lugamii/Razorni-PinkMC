package dev.razorni.hcfactions.reclaims;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ReclaimManager extends Manager {
    private final Map<Integer, Reclaim> reclaims;

    public ReclaimManager(HCF plugin) {
        super(plugin);
        this.reclaims = new HashMap<>();
        this.load();
    }

    public Reclaim getReclaim(Player player) {
        int i = -1;
        for (Reclaim reclaim : this.reclaims.values()) {
            String name = "azurite.reclaim." + reclaim.getName().toLowerCase();
            if (player.hasPermission(name) && reclaim.getPriority() > i) {
                i = reclaim.getPriority();
            }
        }
        return this.reclaims.get(i);
    }

    private void load() {
        for (String s : this.getReclaimsConfig().getConfigurationSection("RECLAIMS").getKeys(false)) {
            Reclaim reclaim = new Reclaim(s, this.getReclaimsConfig().getStringList("RECLAIMS." + s + ".COMMANDS"), this.getReclaimsConfig().getInt("RECLAIMS." + s + ".PRIORITY"));
            this.reclaims.put(reclaim.getPriority(), reclaim);
        }
    }
}
