package dev.razorni.core.extras.coins;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsSetCommand {

    @Command(names = "coins set", permission = "gravity.command.coinsmanager", async = true, description = "Set coins to player.")
    public static void setcoins(CommandSender sender, @Param(name = "target") Player player, @Param(name = "amount") int amount) {
        Profile.getByUuid(player.getUniqueId()).setCoins(amount);
        sender.sendMessage(CC.translate("&fYou have successfully set &6" + amount + " &fcoins to &6" + player.getName() + "."));
        Profile.getByUuid(player.getUniqueId()).save();
    }

}
