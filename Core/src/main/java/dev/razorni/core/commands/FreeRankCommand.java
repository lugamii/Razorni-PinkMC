package dev.razorni.core.commands;

import dev.razorni.core.Core;
import dev.razorni.core.commands.staff.AlertCommand;
import dev.razorni.core.extras.namemc.request.Request;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 11:06 PM
 * Core / rip.orbit.gravity.essentials.command
 */
public class FreeRankCommand {

	@Command(names = {"claimrank", "claimfreerank", "freerank"}, permission = "")
	public static void claimrank(Player sender) {
		Profile profile = Profile.getByUuid(sender.getUniqueId());
		Request request = new Request(sender.getUniqueId(), Core.getInstance().getConfig().getString("NAMEMC.SERVER-IP"));
		if (profile.isNameMcVerified() || Core.getInstance().getVerificationHandler().containsUser(sender.getUniqueId())) {
			sender.sendMessage(CC.translate("&cYou already have your free rank claimed."));
			return;
		}

		if (!request.hasLiked()) {
			sender.sendMessage(CC.translate(""));
			sender.sendMessage(CC.translate("&fTo receive your free &aNameMC&f rank you must vote on &aNameMC&f."));
			sender.sendMessage(CC.translate("&fLink: &d&nhttps://namemc.com/server/" + Core.getInstance().getConfig().getString("NAMEMC.SERVER-IP")));
			sender.sendMessage(CC.translate(""));
			return;
		}

		AlertCommand.alert(sender, CC.translate(Core.getInstance().getConfig().getString("NAMEMC.LIKED-ALERT").replace("%player%", sender.getName())));
		Core.getInstance().getVerificationHandler().addUser(sender.getUniqueId());
		profile.setNameMcVerified(true);
		Core.getInstance().getVerificationHandler().save();

	}

	@Command(names = {"resetnamemc"}, permission = "gravity.command.resetnamemc")
	public static void resetnamemc(Player sender, @Param(name = "target") Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.isNameMcVerified() || Core.getInstance().getVerificationHandler().containsUser(player.getUniqueId())) {
			profile.setNameMcVerified(false);
			Core.getInstance().getVerificationHandler().removeUser(player.getUniqueId());
			Core.getInstance().getVerificationHandler().save();
			sender.sendMessage(CC.translate("&aSuccessfully reset NameMC for " + player.getName()));
		} else {
			sender.sendMessage(CC.translate("&cUser did not like NameMC page."));
		}

	}

}
