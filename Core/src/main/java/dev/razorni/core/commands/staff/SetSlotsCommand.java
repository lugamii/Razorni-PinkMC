package dev.razorni.core.commands.staff;

import dev.razorni.core.Core;
import dev.razorni.core.util.BukkitReflection;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import org.bukkit.command.CommandSender;

public class SetSlotsCommand {

	@Command(names = "setslots", async = true, permission = "gravity.command.setslots")
	public static void setslots(CommandSender sender, @Param(name = "slots") int slots) {
		BukkitReflection.setMaxPlayers(Core.getInstance().getServer(), slots);
		sender.sendMessage(CC.WHITE + "You set the " + CC.GOLD + "max slots " + CC.WHITE + "to " + slots + ".");

	}
}
