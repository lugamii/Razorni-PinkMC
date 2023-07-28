package dev.razorni.gkits.customenchant.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class HellForgedEnchant extends CustomEnchant {

    @Override
    public String getName() {
        return "Hell Forged";
    }

    @Override
    public String getDisplayName() {
        return COLOR + "Hell Forged IV";
    }

    @Override
    public void apply(Player player) {
        player.setMetadata("hellforged", new FixedMetadataValue(GKits.get(), GKits.get()));
    }

    @Override
    public void remove(Player player) {
        player.removeMetadata("hellforged", GKits.get());
    }

    @Override
    public String[] appliesTo() {
        return new String[]{
                "BOOTS",
                "CHESTPLATE",
                "LEGGINGS",
                "HELMET",
                "SWORD"
        };
    }

    @Override
    public String getDescription() {
        return CC.translate("&7&l(&6&l!&7&l)&r &dRepairs your armor when you get hit by an enemy.");
    }


}
