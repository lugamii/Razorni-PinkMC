package dev.razorni.core.extras.rank.command;
import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.razorni.core.database.redis.packets.rank.RankRemoveInheritPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;
import dev.razorni.core.extras.rank.Rank;

public class RankRemoveInheritanceCommand {

	@Command(names = { "rank removeinheritance", "rank removeinherit" }, permission = "gravity.command.rank", async = true, description = "Remove an inheritance to an existing created rank.")
	public static void execute(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "rank") Rank inherit) {
		if (!rank.getInherited().contains(inherit)) {
			sender.sendMessage(CC.RED + "That rank does not have that inheritance.");
			return;
		}

		rank.getInherited().remove(inherit);
		rank.save();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setupPermissionsAttachment(Core.getInstance(), player);
		}

		new RankRemoveInheritPacket(rank, inherit).send();

		sender.sendMessage(CC.GREEN + "Successfully removed that inheritance from %rank%.".replace("%rank%", rank.getDisplayName()));
	}

}
