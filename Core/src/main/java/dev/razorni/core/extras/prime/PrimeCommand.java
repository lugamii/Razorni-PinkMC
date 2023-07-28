package dev.razorni.core.extras.prime;

import dev.razorni.core.extras.prime.menu.PrimeCategoryMenu;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrimeCommand {

    @Command(names = "prime set", permission = "gravity.command.setprime")
    public static void setprime(CommandSender sender, @Param(name = "target") Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isPrime()) {
            profile.setPrime(true);
            profile.save();
            player.sendMessage(CC.translate("&fYour &6Prime &fstatus has been updated to &apurchased."));
            sender.sendMessage(CC.translate("&aYou have set Prime status for " + player.getName() + " to true."));
        } else {
            profile.setPrime(false);
            profile.save();
            player.sendMessage(CC.translate("&fYour &6Prime &fstatus has been updated to &cnot purchased."));
            sender.sendMessage(CC.translate("&aYou have set Prime status for " + player.getName() + " to false."));
        }
    }

    @Command(names = "prime shop", permission = "")
    public static void openshop(final Player player) {
        new PrimeCategoryMenu().openMenu(player);
    }

    @Command(names = "prime dailyreset", permission = "gravity.command.dailyreset")
    public static void dailyreset(CommandSender sender, @Param(name = "target") Player player) {
        Profile.getByUuid(player.getUniqueId()).setPrimedaily(0L);
        Profile.getByUuid(player.getUniqueId()).save();
        player.sendMessage(CC.translate("&aYour Prime Daily cooldown has been reset."));
        sender.sendMessage(CC.translate("&aYou have successfully reset Prime Daily cooldown for " + player.getName()));
    }
}
