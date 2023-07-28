package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.util.command.Param;

public class TeleportHereCommand {

    @Command(names = {"tphere", "tph", "s"}, permission = "gravity.command.teleporthere")
    public static void tphere(Player player, @Param(name = "player") Player target) {
        target.teleport(player);
        player.sendMessage(CC.translate(CC.translate("&fTeleported &6%target% &fto &6%sender%&f.")).replace("%target%", target.getDisplayName()).replace("%sender%", player.getDisplayName()));
    }
}
