package dev.razorni.core.extras.rank.command;

import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import dev.razorni.core.database.redis.packets.rank.RankSetPrefixPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankSetPrefixCommand {

	@Command(names = "rank setprefix", permission = "gravity.command.rank", async = true, description = "Sets the display prefix of an existing rank.")

	public static void setPrefix(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "prefix", wildcard = true) String prefix) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		rank.setPrefix(CC.translate(prefix));
		rank.save();

		for (Profile p : Profile.getProfiles().values()) {
			if (p.getActiveRank().getDisplayName().equals(rank.getDisplayName())) {
				p.getPlayer().setDisplayName(rank.getPrefix() + p.getUsername() + rank.getSuffix());
			}
		}

		new RankSetPrefixPacket(rank, prefix).send();

		Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been reprefixed &6(Global Update) &7&o(" + prefix + ")"), "gravity.staff");
		Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been reprefixed &6(Global Update) &7&o(" + prefix + ")"));

		sender.sendMessage(CC.GREEN + "You updated the rank's prefix.");
	}

}
