package dev.razorni.core.extras.reminder.command;


import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.global.menu.ViewNotificationsMenu;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 6:31 PM
 * Core / rip.orbit.gravity.profile.reminder.command
 */

public class RemindersCommand {

	@Command(names = {"reminders", "reminder", "notification", "notifications"}, permission = "")
	public static void reminders(Player sender) {
		Profile profile = Profile.getByUuid(sender.getUniqueId());
		new ViewNotificationsMenu(profile).openMenu(sender);
	}

}
