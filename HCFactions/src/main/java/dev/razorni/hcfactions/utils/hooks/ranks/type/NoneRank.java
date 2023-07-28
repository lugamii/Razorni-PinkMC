package dev.razorni.hcfactions.utils.hooks.ranks.type;

import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class NoneRank implements Rank {
    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }

    @Override
    public String getRankName(Player player) {
        return "";
    }

    @Override
    public String getRankPrefix(Player player) {
        return "";
    }
}
