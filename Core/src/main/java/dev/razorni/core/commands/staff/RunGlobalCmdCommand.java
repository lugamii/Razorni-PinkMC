package dev.razorni.core.commands.staff;
import dev.razorni.core.database.redis.packets.global.GlobalCommandPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/07/2021 / 5:54 AM
 * Core / rip.orbit.gravity.essentials.command
 */

public class RunGlobalCmdCommand {

	@Command(names = "runglobalcommand", permission = "gravity.command.globalcommand")
	public static void runglobalcommand(CommandSender sender, @Param(name = "command", wildcard = true) String command) {
		if (sender instanceof Player)
			return;

		new GlobalCommandPacket(command).send();
//		new GlobalCommandPacket(new JsonBuilder().addProperty("command",  command)).send();

		sender.sendMessage(CC.translate("&aExecuted the command &7&o'" + command + "' &athroughout all servers."));
	}

}
