package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/08/2021 / 1:03 PM
 * Core / rip.orbit.gravity.commands.staff
 */

public class SudoCommand {

	@Command(names = {"sudo", "makerun", "makethatfuckingkidchatsomething"}, permission = "gravity.command.sudo")
	public static void sudo(CommandSender sender, @Param(name = "target") Player target, @Param(name = "command", wildcard = true) String command) {
		target.chat(command);
		target.sendMessage(CC.translate("&aMade " + target.getName() + " run the command " + command));
	}

}
