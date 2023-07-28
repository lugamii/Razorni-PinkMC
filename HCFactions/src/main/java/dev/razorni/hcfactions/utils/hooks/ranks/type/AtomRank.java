package dev.razorni.hcfactions.utils.hooks.ranks.type;

import com.broustudio.AtomAPI.AtomAPI;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class AtomRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return AtomAPI.getInstance().rankManager.getRank(player.getUniqueId());
    }

    @Override
    public String getRankColor(Player player) {
        return AtomAPI.getInstance().rankManager.getRankColor(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return AtomAPI.getInstance().rankManager.getRankPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return AtomAPI.getInstance().rankManager.getRankSuffix(player.getUniqueId());
    }
}
