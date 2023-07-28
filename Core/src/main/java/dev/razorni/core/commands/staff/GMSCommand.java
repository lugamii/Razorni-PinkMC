package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/07/2021 / 4:50 AM
 * Core / rip.orbit.gravity.commands.staff
 */

public class GMSCommand {

	@Command(names = "gms", permission = "gravity.command.gamemode")
	public static void gms(Player sender) {
		sender.setGameMode(GameMode.SURVIVAL);
		sender.sendMessage(CC.translate("&fYour gamemode has been updated to &6Survival."));
	}

}
