package dev.razorni.gkits.customenchant.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.customenchant.CustomEnchant;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class SpeedEnchant extends CustomEnchant {

    @Override
    public String getName() {
        return "Speed";
    }

    @Override
    public String getDisplayName() {
        return COLOR + "Speed II";
    }

    @Override
    public void apply(Player player) {
        boolean override = false;

        if (!player.getActivePotionEffects().isEmpty())
            override = player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                Integer.MAX_VALUE, 1), override);
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    @Override
    public String[] appliesTo() {
        return new String[]{
                "BOOTS"
        };
    }

    @Override
    public String getDescription() {
        return CC.translate("&7&l(&6&l!&7&l)&r &dGives the player constant Speed II.");
    }


}
