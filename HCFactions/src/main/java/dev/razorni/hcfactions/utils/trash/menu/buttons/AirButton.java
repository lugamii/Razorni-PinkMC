package dev.razorni.hcfactions.utils.trash.menu.buttons;

import dev.razorni.hcfactions.utils.trash.item.ItemBuilder;
import dev.razorni.hcfactions.utils.trash.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AirButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.AIR).build();
    }

    @Override
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }
}
