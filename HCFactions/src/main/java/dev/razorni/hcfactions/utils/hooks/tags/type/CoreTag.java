package dev.razorni.hcfactions.utils.hooks.tags.type;

import com.broustudio.CoreAPI.CoreAPI;
import dev.razorni.hcfactions.utils.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class CoreTag implements Tag {
    @Override
    public String getTag(Player player) {
        String s = CoreAPI.plugin.tagManager.getTagDisplay(player.getUniqueId());
        return (s != null) ? s : "";
    }
}
