package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import org.bukkit.command.CommandSender;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 6:08 PM
 * Core / rip.orbit.gravity.commands.staff
 */
public class UniqueLoginsCommand {

	@Command(names = {"allplayers", "logins", "uniqueplayers"}, permission = "op")
	public static void uniqueLogins(CommandSender sender) {
		long size = Profile.getCollection().countDocuments();

		sender.sendMessage(CC.translate("&eThere has been &6" + size + "&e unique logins to the minecraft pvp server lewl."));
	}

}
