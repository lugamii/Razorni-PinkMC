package dev.razorni.hcfactions.extras.cshop;

import dev.razorni.hcfactions.extras.cshop.menu.VirtualCategoryMenu;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import org.bukkit.entity.Player;

public class CoinsShopCommand {

    @Command(names = "coinshop", permission = "")
    public static void coins(final Player player) {
        new VirtualCategoryMenu().openMenu(player);
    }

}
