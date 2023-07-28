package dev.razorni.gkits.customenchant.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collections;
import java.util.List;

public class ImplantsEnchant extends CustomEnchant {

    @Override
    public String getName() {
        return "Implants";
    }

    @Override
    public String getDisplayName() {
        return COLOR + "Implants V";
    }

    @Override
    public void apply(Player player) {
        player.setMetadata("implants", new FixedMetadataValue(GKits.get(), GKits.get()));
    }

    @Override
    public void remove(Player player) {
        player.removeMetadata("implants", GKits.get());
    }

    @Override
    public String[] appliesTo() {
        return new String[]{
                "HELMET"
        };
    }

    @Override
    public String getDescription() {
        return CC.translate("&7&l(&6&l!&7&l)&r &dAutomatically keeps you saturated.");
    }

}
