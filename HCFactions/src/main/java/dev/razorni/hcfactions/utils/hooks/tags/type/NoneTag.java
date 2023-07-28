package dev.razorni.hcfactions.utils.hooks.tags.type;

import dev.razorni.hcfactions.utils.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class NoneTag implements Tag {
    @Override
    public String getTag(Player player) {
        return "";
    }
}
