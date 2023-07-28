package dev.razorni.core.extras.coins;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.Locale;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand {

    @Command(names = "coins", permission = "")
    public static void coins(CommandSender sender, @Param(name = "target", defaultValue = "self") Player player) {
        player.sendMessage(CC.translate("&6Coins: &f" + Profile.getByUuid(player.getUniqueId()).getCoins() + " ⛁"));
    }

}
