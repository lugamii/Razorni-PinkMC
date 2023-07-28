package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;

public class TeleportCommand {

    @Command(names = {"tp", "teleport"}, permission = "gravity.command.teleport")
    public static void tp(Player player, @Param(name = "target") Player target) {
        player.teleport(target);
        player.sendMessage(CC.translate(CC.translate("&fTeleported to &6%target%&f.")).replace("%target%", target.getDisplayName()));
    }
}
