package dev.razorni.hcfactions.commands.type;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import dev.razorni.hcfactions.utils.Crowbar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrowbarCommand {

    @Command(names = "crowbar give", permission = "hcf.command.crowbar")
    public boolean crowbar(CommandSender commandSender, @Param(name = "player") Player target) {
        ItemStack itemStack = new Crowbar().toItemStack().get();
        target.getInventory().addItem(itemStack);
        target.sendMessage(ChatColor.RED + commandSender.getName() + " has given you a crowbar.");
        commandSender.sendMessage(ChatColor.RED + "You have given " + target.getName() + " a crowbar.");
        return true;
    }
}
