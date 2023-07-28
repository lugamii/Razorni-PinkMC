package dev.razorni.core.profile.option.staff.command;



import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.option.staff.menu.StaffHistoryMenu;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 2:28 PM
 * Core / rip.orbit.gravity.profile.option.staff.command
 */

public class StaffInfoCommand {

	@Command(names = "staffinfo", permission = "gravity.command.staffinfo")
	public static void staffinfo(Player sender, @Param(name = "target") UUID uuid) {

		new StaffHistoryMenu(Profile.getByUuid(uuid)).openMenu(sender);

	}

}
