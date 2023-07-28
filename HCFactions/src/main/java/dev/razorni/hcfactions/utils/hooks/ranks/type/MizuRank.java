package dev.razorni.hcfactions.utils.hooks.ranks.type;

import com.broustudio.MizuAPI.MizuAPI;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class MizuRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return MizuAPI.getAPI().getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return MizuAPI.getAPI().getRankPrefix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }

    @Override
    public String getRankSuffix(Player player) {
        return MizuAPI.getAPI().getRankSuffix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }

    @Override
    public String getRankColor(Player player) {
        return MizuAPI.getAPI().getRankColor(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }
}
