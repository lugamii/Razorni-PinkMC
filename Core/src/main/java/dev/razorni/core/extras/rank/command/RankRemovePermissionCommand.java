package dev.razorni.core.extras.rank.command;
import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.razorni.core.database.redis.packets.rank.RankRemovePermPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

public class RankRemovePermissionCommand {

	@Command(names = { "rank removepermission", "rank removeperm" }, permission = "rank.manager", async = true, description = "Removes a permission from an existing rank.")

	public static void removeperm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "permission") String permission) {
		if (!rank.hasPermission(permission)) {
			sender.sendMessage(CC.RED + "That rank does not have that permission.");
			return;
		}

		rank.getPermissions().remove(permission);
		rank.save();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setupPermissionsAttachment(Core.getInstance(), player);
		}

		new RankRemovePermPacket(rank, permission).send();


		sender.sendMessage(CC.GREEN + "Successfully removed permission from rank.");
	}

}
