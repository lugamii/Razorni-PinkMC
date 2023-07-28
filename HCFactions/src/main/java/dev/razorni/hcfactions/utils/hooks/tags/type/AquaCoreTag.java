package dev.razorni.hcfactions.utils.hooks.tags.type;

import dev.razorni.core.profile.Profile;
import dev.razorni.hcfactions.utils.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class AquaCoreTag implements Tag {
    @Override
    public String getTag(Player player) {
        if (Profile.getByUuid(player.getUniqueId()).getTag() != null) {
            return Profile.getByUuid(player.getUniqueId()).getTag().getPrefix() + " ";
        } else {
            return "";
        }
    }
}
