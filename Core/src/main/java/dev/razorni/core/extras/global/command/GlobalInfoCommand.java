package dev.razorni.core.extras.global.command;


import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.global.menu.ProfileMainMenu;
import dev.razorni.core.util.command.Param;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 11:38 PM
 * Core / rip.orbit.gravity.profile.global.command
 */

public class GlobalInfoCommand {

	@Command(names = {"globalinfo", "profile"}, permission = "")
	public static void globalinfo(Player sender, @Param(name = "target", defaultValue = "self") UUID uuid) {
		new ProfileMainMenu(Profile.getByUuid(uuid)).openMenu(sender);
	}

}
