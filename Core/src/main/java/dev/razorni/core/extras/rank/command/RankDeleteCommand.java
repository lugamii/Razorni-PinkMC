package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import dev.razorni.core.database.redis.packets.rank.RankDeletePacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.util.Locale;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankDeleteCommand {

	@Command(names = {"rank delete"}, permission = "gravity.command.rank", async = true)
	public static void delete(CommandSender sender, @Param(name = "rank") Rank rank) {
		if (rank == null) {
			sender.sendMessage(Locale.RANK_NOT_FOUND.format());
			return;
		}

		rank.delete();

		new RankDeletePacket(rank).send();

		sender.sendMessage(CC.GREEN + "You deleted the rank.");
	}

}
