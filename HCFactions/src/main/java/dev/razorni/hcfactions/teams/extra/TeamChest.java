package dev.razorni.hcfactions.teams.extra;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class TeamChest {
    private final double percentage;
    private final ItemStack itemStack;

    public TeamChest(ItemStack itemStack, double percentage) {
        this.itemStack = itemStack;
        this.percentage = percentage;
    }
}