package dev.razorni.core.extras.friends.command;

import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.global.menu.ViewFriendsMenu;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 7:43 PM
 * Core / rip.orbit.gravity.profile.friends.command
 */

public class FriendCommand {
	@Command(names = "friends", permission = "")
	public static void friends(Player sender) {
		new ViewFriendsMenu(Profile.getByUuid(sender.getUniqueId())).openMenu(sender);
	}
}
