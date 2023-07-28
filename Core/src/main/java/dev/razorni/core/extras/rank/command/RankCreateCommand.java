package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import dev.razorni.core.database.redis.packets.rank.RankCreatePacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankCreateCommand {

	@Command(names = "rank create", permission = "gravity.command.rank", description = "Create a new rank.")
	public static void create(CommandSender sender, @Param(name = "rank") String name) {
		if (Rank.getRankByDisplayName(name) != null) {
			sender.sendMessage(CC.RED + "A rank with that name already exists.");
			return;
		}

		Rank rank = new Rank(name);
		rank.save();

		new RankCreatePacket(rank).send();

		sender.sendMessage(CC.GREEN + "You created a new rank.");
	}

}
