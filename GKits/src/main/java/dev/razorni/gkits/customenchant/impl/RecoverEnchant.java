package dev.razorni.gkits.customenchant.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.customenchant.CustomEnchant;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RecoverEnchant extends CustomEnchant {

    @Override
    public String getName() {
        return "Recover";
    }

    @Override
    public String getDisplayName() {
        return COLOR + "Recover I";
    }

    @Override
    public void apply(Player player) {

    }

    @Override
    public void remove(Player player) {

    }

    @Override
    public String[] appliesTo() {
        return new String[]{
                "CHESTPLATE"
        };
    }

    @Override
    public String getDescription() {
        return CC.translate("&7&l(&6&l!&7&l)&r &dGives the player absorption when a player is killed.");
    }


}
