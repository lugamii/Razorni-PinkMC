package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import dev.razorni.core.database.redis.packets.rank.RankSetWeightPacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankSetWeightCommand {

	@Command(names = "rank setweight", permission = "gravity.command.rank", async = true, description = "Sets the weight of an existing rank.")
	public static void setweight(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "weight") int weight) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		rank.setWeight(weight);
		rank.save();

		new RankSetWeightPacket(rank, weight).send();

		sender.sendMessage(CC.GREEN + "You updated the rank's weight.");
	}

}
