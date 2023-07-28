package dev.razorni.core.extras.rank.command;


import dev.razorni.core.util.CC;
import org.bukkit.command.CommandSender;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.apache.commons.lang.StringUtils;
import dev.razorni.core.extras.rank.Rank;

import java.util.ArrayList;
import java.util.List;

public class RankDumpCommand {
	@Command(names = "rank info", permission = "gravity.command.rank.info", async = true, description = "Show info on a specific rank.")
	public static void dump(CommandSender sender, @Param(name = "rank") Rank rank) {
		sender.sendMessage(rank.getDisplayName() + "'s Information");

		List<String> inherited = new ArrayList<>();
		List<String> inheritedPerms = new ArrayList<>();
		rank.getInherited().forEach(r -> inherited.add(r.getDisplayName()));
		for (Rank inherit : rank.getInherited()) {
			inheritedPerms.addAll(inherit.getAllPermissions());
		}

		sender.sendMessage(CC.translate("&6Inheritances&7: " + StringUtils.join(inherited, ", ")));
		sender.sendMessage(CC.translate("&6Color&7: " + rank.getColor() + "Color"));
		sender.sendMessage(CC.translate("&6Weight&7: " + rank.getWeight()));
		sender.sendMessage(CC.translate("&6Prefix&7: " + rank.getPrefix()));
		sender.sendMessage(CC.translate("&6Display&7: " + rank.getDisplayName()));
		sender.sendMessage(CC.translate("&6Permissions&7: " + StringUtils.join(rank.getPermissions(), ", ")));
		sender.sendMessage(CC.translate("&6Inherited Permissions&7: " + StringUtils.join(inheritedPerms, ", ")));
	}
}
