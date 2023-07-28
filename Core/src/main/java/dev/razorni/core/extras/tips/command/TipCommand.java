package dev.razorni.core.extras.tips.command;


import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/07/2021 / 12:54 AM
 * Core / rip.orbit.gravity.profile.tips.command
 */

public class TipCommand {

	@Command(names = "reloadtips", permission = "op")
	public static void reloadTips(CommandSender sender) {
		Core.getInstance().getTipManager().getTips().clear();

		Core.getInstance().getTipManager().getFile().reload();

		for (String sect : Core.getInstance().getTipManager().getFile().getConfigurationSection("tips").getKeys(false)) {
			Core.getInstance().getTipManager().getTips().add(Core.getInstance().getTipManager().getFile().getStringList("tips." + sect));
		}
	}

}
