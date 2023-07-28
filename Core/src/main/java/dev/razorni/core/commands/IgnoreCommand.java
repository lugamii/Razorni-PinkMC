package dev.razorni.core.commands;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/08/2021 / 2:12 PM
 * Core / rip.orbit.gravity.essentials.command
 */

public class IgnoreCommand {

	@Command(names = {"ignore", "block"}, permission = "")
	public static void ignore(Player sender, @Param(name = "player")Profile target) {

		Profile senderProfile = Profile.getByUuid(sender.getUniqueId());

		if (senderProfile.getIgnored().contains(target.getUuid())) {
			sender.sendMessage(CC.translate("&cYou already have that player ignored."));
			return;
		}

		senderProfile.getIgnored().add(target.getUuid());
		senderProfile.save();
		sender.sendMessage(CC.translate("&a" + target.getUsername() + " has been successfully ignored."));

	}

}
