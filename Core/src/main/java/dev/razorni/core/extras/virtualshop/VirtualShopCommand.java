package dev.razorni.core.extras.virtualshop;

import dev.razorni.core.extras.virtualshop.menu.VirtualMenuCategory;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class VirtualShopCommand {

    @Command(names = {"buy", "store"}, permission = "")
    public static void virtualshop(CommandSender sender, @Param(name = "target", defaultValue = "self") Player player) {
        new VirtualMenuCategory().openMenu(player);
    }

}
