package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import dev.razorni.core.database.redis.packets.rank.RankSetSuffixPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankSetSuffixCommand {

	@Command(names = "rank setsuffix", permission = "gravity.command.rank", async = true, description = "Sets the display suffix of an existing rank.")

	public static void setPrefix(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "suffix") String suffix) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		rank.setSuffix(CC.translate(suffix));
		rank.save();

		for (Profile p : Profile.getProfiles().values()) {
			if (p.getActiveRank().getDisplayName().equals(rank.getDisplayName())) {
				p.getPlayer().setDisplayName(rank.getPrefix() + p.getUsername() + rank.getSuffix());
			}
		}

		new RankSetSuffixPacket(rank, suffix).send();

		Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been reprefixed &6(Global Update) &7&o(" + suffix + ")"), "gravity.staff");
		Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been reprefixed &6(Global Update) &7&o(" + suffix + ")"));

		sender.sendMessage(CC.GREEN + "You updated the rank's suffix.");
	}

}
