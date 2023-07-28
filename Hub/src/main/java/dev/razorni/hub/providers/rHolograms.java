package dev.razorni.hub.providers;

import dev.razorni.core.Core;
import dev.razorni.hub.Hub;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class rHolograms extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "hub";
    }

    @Override
    public String getAuthor() {
        return "Razorni";
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("rank")) {
            return Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player);
        }
        if (identifier.equalsIgnoreCase("queued-hcf")) {
            return String.valueOf(Hub.getInstance().getQueueManager().getInQueue("HCF"));
        }

        return "Invalid Placeholder";
    }

}
