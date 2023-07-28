package dev.razorni.core.commands.staff;

import dev.razorni.core.util.ItemUtils;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {
    @Command(names={"item", "i", "get"}, permission="foxtrot.give", description="Spawn yourself an item")
    public static void item(Player sender, @Param(name="item") ItemStack item, @Param(name="amount", defaultValue="64") int amount) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return;
        }
        item.setAmount(amount);
        sender.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.WHITE + amount + ChatColor.GOLD + " of " + ChatColor.WHITE + ItemUtils.getName( item) + ChatColor.GOLD + ".");
    }
}

