package dev.razorni.hcfactions.staff.extra;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class StaffItem {
    private final String replacement;
    private final int slot;
    private final StaffItemAction action;
    private final ItemStack item;

    public StaffItem(StaffItemAction action, String replacement, ItemStack item, int slot) {
        this.action = action;
        this.replacement = replacement;
        this.item = item;
        this.slot = slot;
    }
}