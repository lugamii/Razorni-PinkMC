package dev.razorni.core.commands.staff;


import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.commands.menu.ReportsMenu;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 7:06 AM
 * Core / rip.orbit.gravity.essentials.command
 */

public class ReportsCommand {

	@Command(names = "reports", permission = "gravity.staff")
	public static void reports(Player sender) {
		new ReportsMenu().openMenu(sender);
	}

}
