package dev.razorni.core.extras.rank.command;
import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.razorni.core.database.redis.packets.rank.RankAddInheritPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import dev.razorni.core.extras.rank.Rank;

public class RankAddInheritanceCommand {

	@Command(names = {"rank addinheritance", "rank inherit", "rank addinherit"}, permission = "gravity.command.rank", async = true, description = "Add an inheritance to an existing created rank.")
	public static void addinherit(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "rank") Rank inherit) {
		if (rank.getInherited().contains(inherit)) {
			sender.sendMessage(CC.RED + "That rank already has that inheritance.");
			return;
		}

		rank.getInherited().add(inherit);
		rank.save();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setupPermissionsAttachment(Core.getInstance(), player);
		}

		new RankAddInheritPacket(rank, inherit).send();

		sender.sendMessage(CC.GREEN + "Successfully added that inheritance to %rank%.".replace("%rank%", rank.getDisplayName()));
	}

}
