package dev.razorni.core.commands;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/08/2021 / 7:35 PM
 * Core / rip.orbit.gravity.essentials.command
 */
public class SoundsCommand {

	@Command(names = {"sounds", "sound", "togglesounds", "togglemsgsounds"}, permission = "")
	public static void sounds(Player sender) {
		Profile profile = Profile.getByUuid(sender.getUniqueId());

		boolean toggle = !profile.getOptions().isSoundsEnabled();

		String status = (toggle ? "&aon" : "&coff");

		sender.sendMessage(CC.translate("&fYou have just toggled your &6message sounds&f " + status + "&f."));

		profile.getOptions().setSoundsEnabled(toggle);
		profile.save();
	}

}
