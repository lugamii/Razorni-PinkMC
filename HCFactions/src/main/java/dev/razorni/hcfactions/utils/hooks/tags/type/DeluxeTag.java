package dev.razorni.hcfactions.utils.hooks.tags.type;

import dev.razorni.hcfactions.utils.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class DeluxeTag implements Tag {
    @Override
    public String getTag(Player player) {
        return me.clip.deluxetags.DeluxeTag.getPlayerDisplayTag(player);
    }
}
