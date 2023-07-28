package dev.razorni.hcfactions.utils.commandapi.command.parameter;

import com.google.common.collect.ImmutableList;
import dev.razorni.hcfactions.utils.commandapi.command.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.razorni.hcfactions.utils.commandapi.ItemUtils;

import java.util.List;
import java.util.Set;

public class ItemStackParameterType implements ParameterType<ItemStack> {
    public ItemStackParameterType() {
    }

    public ItemStack transform(CommandSender sender, String source) {
        ItemStack item = ItemUtils.get(source);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No item with the name " + source + " found.");
            return null;
        } else {
            return item;
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}
