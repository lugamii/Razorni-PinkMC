package dev.razorni.core.profile.command;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import dev.razorni.core.profile.Profile;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 1:40 AM
 * Core / rip.orbit.gravity.profile.command
 */

public class FreezeCommand {

	@Command(names = {"awdwada", "awdwadwa", "awdwawa"}, async = true, permission = "gravity.command.freeze")

	public static void freeze(CommandSender sender, @Param(name = "target") Profile profile) {

		if (profile.getOptions().isFrozen()) {
			profile.getOptions().setFrozen(false);
			sender.sendMessage(CC.translate("&aYou have just unfroze " + profile.getUsername()));
		} else {
			profile.getOptions().setFrozen(true);
			profile.getPlayer().sendMessage(CC.CHAT_BAR);
			profile.getPlayer().sendMessage(CC.translate("&fJoin &6ts.orbit.rip&f you have &63 minutes&f to join."));
			profile.getPlayer().sendMessage(CC.translate("&fIf you fail to join within that time you will be &6banned&f."));
			profile.getPlayer().sendMessage(CC.CHAT_BAR);
			sender.sendMessage(CC.translate("&aYou have just froze " + profile.getUsername()));
		}
		profile.save();
	}

}
