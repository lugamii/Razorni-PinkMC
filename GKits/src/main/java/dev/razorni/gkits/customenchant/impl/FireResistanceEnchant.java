package dev.razorni.gkits.customenchant.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.customenchant.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class FireResistanceEnchant extends CustomEnchant {

    @Override
    public String getName() {
        return "Fire Resistance";
    }

    @Override
    public String getDisplayName() {
        return COLOR + "Fire Resistance I";
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
                Integer.MAX_VALUE, 0), player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE));
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
    }

    @Override
    public String[] appliesTo() {
        return new String[]{
                "LEGGINGS",
                "CHESTPLATE",
                "HELMET",
                "BOOTS"
        };
    }

    @Override
    public String getDescription() {
        return CC.translate("&7&l(&6&l!&7&l)&r &dGives the player constant fire resistance.");
    }


}
