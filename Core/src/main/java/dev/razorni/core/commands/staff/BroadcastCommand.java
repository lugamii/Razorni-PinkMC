package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BroadcastCommand {

	@Command(names = {"bc", "broadcast"}, permission = "gravity.command.broadcast")
	public static void broadcast(CommandSender sender, @Param(name = "message", wildcard = true) String message) {
		Bukkit.broadcastMessage(CC.translate(message));
	}

}
