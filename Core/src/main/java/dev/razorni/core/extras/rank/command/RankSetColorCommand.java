package dev.razorni.core.extras.rank.command;
import dev.razorni.core.util.CC;
import dev.razorni.core.database.redis.packets.rank.RankSetColorPacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankSetColorCommand {

	@Command(names = "rank setcolor", permission = "gravity.command.rank", async = true, description = "Sets the display color of an existing rank.")
	public static void setcolor(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "color") String color) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		try {
			rank.setColor(ChatColor.valueOf(color.toUpperCase()));
			rank.save();
		} catch (Exception e) {
			sender.sendMessage(CC.RED + "That color is not valid.");
			return;
		}

		new RankSetColorPacket(rank, ChatColor.valueOf(color.toUpperCase())).send();

		sender.sendMessage(CC.GREEN + "You updated the rank's color.");
	}

}
