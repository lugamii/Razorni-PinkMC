package dev.razorni.hcfactions.utils.hooks.ranks;

import org.bukkit.entity.Player;

public interface Rank {
    String getRankColor(Player p0);

    String getRankPrefix(Player p0);

    String getRankSuffix(Player p0);

    String getRankName(Player p0);
}
