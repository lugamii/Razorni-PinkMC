package dev.razorni.hcfactions.events.eotw.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EotwCommand {

    @Command(names = "eotw", permission = "azurite.eotw")
    public static void eotw(final Player player) {
        boolean newStatus = !HCF.getPlugin().getEotwHandler().isEndOfTheWorld(false);
        player.sendMessage(ChatColor.GREEN + "Set EOTW mode to " + newStatus + '.');
        HCF.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
    }

}
