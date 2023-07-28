package dev.razorni.core.profile.punishment.command;


import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import dev.razorni.core.util.command.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/08/2021 / 1:09 PM
 * Core / rip.orbit.gravity.profile.punishment.command"
 */

public class IPReportsCommand {

	@Command(names = {"ipreports", "ipreport", "dupeips", "showevades", "altsreports", "altreports"}, permission = "gravity.command.ipreport", async = true)
	public static void ipreport(Player sender) {
		sender.sendMessage(CC.translate(" "));
		sender.sendMessage(CC.translate("&6&lOnline Profile Alt Report"));
		sender.sendMessage(CC.translate("&7(&7Offline &f- &aOnline &f- &cBanned &f- &4Blacklisted&7)"));
		sender.sendMessage(CC.translate(" "));

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile != null) {
				sender.sendMessage(CC.translate(profile.getColoredUsername() + "'s Alts &7(" + StringUtils.join(profile.colorAlts(), ", ") + "&7)"));
			}
		}
	}

}
