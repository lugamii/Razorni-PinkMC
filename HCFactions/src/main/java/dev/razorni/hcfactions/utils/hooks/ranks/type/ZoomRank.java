package dev.razorni.hcfactions.utils.hooks.ranks.type;

import club.frozed.core.ZoomAPI;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class ZoomRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return ZoomAPI.getRankName(player);
    }

    @Override
    public String getRankPrefix(Player player) {
        return ZoomAPI.getRankPrefix(player);
    }

    @Override
    public String getRankColor(Player player) {
        return ZoomAPI.getRankColor(player).toString();
    }

    @Override
    public String getRankSuffix(Player player) {
        return ZoomAPI.getRankSuffix(player);
    }
}
