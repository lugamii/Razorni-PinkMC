package dev.razorni.hcfactions.extras.framework.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
    public abstract void onClick(InventoryClickEvent p0);

    public abstract ItemStack getItemStack();
}