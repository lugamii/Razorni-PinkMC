package dev.razorni.core.extras.rank.command;
import dev.razorni.core.Core;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.razorni.core.database.redis.packets.rank.RankAddPermPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import org.bukkit.command.CommandSender;

public class RankAddPermissionCommand {


	@Command(names = {"rank addperm", "rank addpermission"}, permission = "gravity.command.rank", async = true, description = "Add a permission to an existing created rank.")
	public static void addperm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "permission") String permission) {
		if (rank.hasPermission(permission)) {
			sender.sendMessage(CC.RED + "That rank already has that permission.");
			return;
		}

		rank.getPermissions().add(permission);
		rank.save();
//
//		new RankUpdatePacket(new JsonBuilder()
//				.addProperty("uuid", rank.getUuid().toString())
//				.addProperty("addPerm", true)
//				.addProperty("create", false)
//				.addProperty("permission", permission)
//				.addProperty("name", rank.getDisplayName()))
//				.send();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setupPermissionsAttachment(Core.getInstance(), player);
		}

		new RankAddPermPacket(rank, permission).send();


		sender.sendMessage(CC.GREEN + "Successfully added permission to rank.");
	}

}
