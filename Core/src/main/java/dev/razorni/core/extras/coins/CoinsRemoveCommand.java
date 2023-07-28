package dev.razorni.core.extras.coins;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsRemoveCommand {

    @Command(names = "coins remove", permission = "gravity.command.coinsmanager", async = true, description = "Remove coins from player.")
    public static void setcoins(CommandSender sender, @Param(name = "target") Player player, @Param(name = "amount") int amount) {
        Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - amount);
        sender.sendMessage(CC.translate("&fYou have successfully removed &6" + amount + " &fcoins from &6" + player.getName() + "."));
        Profile.getByUuid(player.getUniqueId()).save();
    }

}
