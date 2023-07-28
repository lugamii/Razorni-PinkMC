package dev.razorni.hcfactions.utils.hooks.ranks.type;

import com.minexd.zoot.profile.Profile;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class ZootRank implements Rank {
    @Override
    public String getRankColor(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getColor().toString();
    }

    @Override
    public String getRankSuffix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getSuffix();
    }

    @Override
    public String getRankName(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getPrefix();
    }
}
