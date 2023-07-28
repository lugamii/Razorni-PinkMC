package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;

public class SpeedCommand {

    @Command(names = {"speed"}, permission = "gravity.command.speed")
    public static void speed(final Player player, @Param(name = "1-10") int amount) {
        if (amount < 1 || amount > 10) {
            player.sendMessage(CC.translate("&cYou can set speed between 1 and 10."));
            return;
        }

        if (player.isFlying()) {
            player.setFlySpeed(amount * 0.1F);
            player.sendMessage(CC.translate("&aYour speed has been set to " + amount + "."));
        } else {
            player.setWalkSpeed(amount * 0.1F);
            player.sendMessage(CC.translate("&aYour speed has been set to " + amount + "."));
        }
    }
}