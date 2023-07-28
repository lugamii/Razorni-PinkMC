package dev.razorni.core.profile.command;


import dev.razorni.core.util.Locale;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.fanciful.FancyMessage;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.apache.commons.lang.StringUtils;
import dev.razorni.core.profile.Profile;
import org.bukkit.command.CommandSender;

public class AltsCommand {

	@Command(names = "alts", async = true, permission = "gravity.command.alts")
	public static void alts(CommandSender sender, @Param(name = "target") Profile profile) {
		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		sender.sendMessage(" ");
		sender.sendMessage(CC.translate("&7(&7Offline &f- &aOnline &f- &cBanned &f- &4Blacklisted&7)"));
		sender.sendMessage(CC.translate("&fAlts&7: &r" + StringUtils.join(profile.colorAlts(), ", ")));
		sender.sendMessage(" ");
		if (sender.isOp()) {
			sender.sendMessage(CC.CHAT_BAR);
			sender.sendMessage(CC.translate("&fCurrent IP&7: &6" + profile.getCurrentAddress()));
			sender.sendMessage(CC.CHAT_BAR);
			FancyMessage parts = new FancyMessage();
			parts.text(CC.translate("&fPast IPs&7: "));
			parts.then().text(CC.translate("&6Hover Over")).tooltip(profile.getIpAddresses());
			parts.send(sender);
		}
		sender.sendMessage(" ");
	}

}
