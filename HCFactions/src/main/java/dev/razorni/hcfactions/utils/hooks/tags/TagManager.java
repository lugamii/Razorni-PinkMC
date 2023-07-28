package dev.razorni.hcfactions.utils.hooks.tags;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.hooks.tags.type.*;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Player;

public class TagManager extends Manager implements Tag {
    private Tag tag;

    public TagManager(HCF plugin) {
        super(plugin);
        this.load();
    }

    @Override
    public String getTag(Player player) {
        return this.tag.getTag(player);
    }

    private void load() {
        if (Utils.verifyPlugin("Core", this.getInstance())) {
            this.tag = new AquaCoreTag();
        } else if (Utils.verifyPlugin("Mizu", this.getInstance())) {
            this.tag = new MizuTag();
        } else if (Utils.verifyPlugin("Basic", this.getInstance())) {
            this.tag = new CoreTag();
        } else if (Utils.verifyPlugin("DeluxeTags", this.getInstance())) {
            this.tag = new DeluxeTag();
        } else {
            this.tag = new NoneTag();
        }
    }
}
