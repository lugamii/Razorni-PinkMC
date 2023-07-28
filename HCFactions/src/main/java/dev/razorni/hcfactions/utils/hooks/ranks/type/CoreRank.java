package dev.razorni.hcfactions.utils.hooks.ranks.type;

import com.broustudio.CoreAPI.CoreAPI;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class CoreRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return CoreAPI.plugin.rankManager.getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return CoreAPI.plugin.rankManager.getRankPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public String getRankColor(Player player) {
        return CoreAPI.plugin.rankManager.getRankColor(player.getUniqueId());
    }
}
