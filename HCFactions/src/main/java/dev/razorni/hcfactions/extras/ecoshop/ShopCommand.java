package dev.razorni.hcfactions.extras.ecoshop;

import dev.razorni.hcfactions.extras.ecoshop.menu.CategorySelectorMenu;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import org.bukkit.entity.Player;

public class ShopCommand {

    @Command(names = "itemshop", permission = "")
    public static void coins(final Player player) {
            new CategorySelectorMenu().openMenu(player);
    }

}
