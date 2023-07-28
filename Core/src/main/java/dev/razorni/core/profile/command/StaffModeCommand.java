package dev.razorni.core.profile.command;

import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;

public class StaffModeCommand {

    @Command(names = { "hubmod", "modhub" }, permission = "gravity.command.mod")
    public static void toggleMod(Player player) {
       if (!Core.getInstance().getStaffManager().isStaffMode(player)) {
           Core.getInstance().getStaffManager().enableStaffMode(player);
       } else {
           Core.getInstance().getStaffManager().disableStaffMode(player);
       }
    }

}
