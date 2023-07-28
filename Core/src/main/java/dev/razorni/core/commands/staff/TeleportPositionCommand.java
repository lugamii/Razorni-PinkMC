package dev.razorni.core.commands.staff;



import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPositionCommand {

    @Command(names = "tppos", permission = "gravity.command.teleportpos")
    public static void tppos(Player player, @Param(name = "x") int x, @Param(name = "y") int y, @Param(name = "z") int z ) {
        Location location = new Location(player.getWorld(), x + 0.5, y, z + 0.5);

        player.teleport(location);
        player.sendMessage(CC.translate(CC.translate("&fTeleported to &fx: %x% y: %y% z:%z%")).replace("%x%", "" + x).replace("%y%", "" + y).replace("%z%", "" + z));

    }
}
