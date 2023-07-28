package dev.razorni.hcfactions.extras.redeem.menu.buttons;

import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GlassFillButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
        return item;
    }

}
