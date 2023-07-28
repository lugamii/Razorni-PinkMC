package dev.razorni.core;

import dev.razorni.core.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CoreAPI {

    public String getTag(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getTag().getPrefix());
    }

    public int getCoins(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getCoins();
    }

    public String coloredUsername(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getColoredUsername());
    }

    public String getRankName(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName());
    }

    public String getRankPrefix(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getActiveRank().getPrefix());
    }

    public String getRankSuffix(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getActiveRank().getSuffix());
    }

    public ChatColor getRankColor(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getColor();
    }


}
