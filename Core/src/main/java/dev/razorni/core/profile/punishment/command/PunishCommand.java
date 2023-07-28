package dev.razorni.core.profile.punishment.command;



import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.menu.punish.PunishMenu;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:43 AM
 * Core / rip.orbit.gravity.profile.punishment.command
 */

public class PunishCommand {

	@Command(names = "punish", permission = "gravity.command.punish", async = true)
	public static void punish(Player sender, @Param(name = "target") Profile target) {
		new PunishMenu(target).openMenu(sender);
	}

}
