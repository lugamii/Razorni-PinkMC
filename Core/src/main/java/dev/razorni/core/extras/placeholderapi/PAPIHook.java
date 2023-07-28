package dev.razorni.core.extras.placeholderapi;

import dev.razorni.core.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PAPIHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "preeisdick";
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
        String heart = "";
        if (identifier.equalsIgnoreCase("coloredrank")) {
            return Profile.getByUuid(player.getUniqueId()).getActiveRank().getColor() + Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName();
        }
        if (identifier.equalsIgnoreCase("rankname")) {
            return Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName();
        }
        if (identifier.equalsIgnoreCase("rankprefix")) {
            return Profile.getByUuid(player.getUniqueId()).getActiveRank().getPrefix();
        }
        if (identifier.equalsIgnoreCase("ranksuffix")) {
            return Profile.getByUuid(player.getUniqueId()).getActiveRank().getSuffix();
        }
        if (identifier.equalsIgnoreCase("tag")) {
            return Profile.getByUuid(player.getUniqueId()).getTag().getPrefix();
        }
        return "Invalid Placeholder";
    }

}
