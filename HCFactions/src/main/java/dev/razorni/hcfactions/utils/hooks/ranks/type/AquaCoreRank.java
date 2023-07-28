package dev.razorni.hcfactions.utils.hooks.ranks.type;

import dev.razorni.core.profile.Profile;
import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class AquaCoreRank implements Rank {
    @Override
    public String getRankColor(Player player) {
        return String.valueOf(Profile.getByUuid(player.getUniqueId()).getActiveRank().getColor());
    }

    @Override
    public String getRankSuffix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getSuffix();
    }

    @Override
    public String getRankPrefix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getPrefix();
    }

    @Override
    public String getRankName(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName();
    }
}
