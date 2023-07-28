package dev.razorni.core.profile.grant.command;

import dev.razorni.core.util.Locale;
import dev.razorni.core.database.redis.packets.GrantAddPacket;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.discord.DiscordLogger;
import dev.razorni.core.util.duration.Duration;
import dev.razorni.core.util.uuid.UniqueIDCache;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class SetRankCommand {

	@Command(names = "setrank", async = true, permission = "gravity.command.setrank")
	public static void setrank(CommandSender sender, @Param(name = "player") UUID target, @Param(name = "rank") Rank rank, @Param(name = "time") String time, @Param(name = "reason") String reason) {
		Duration duration = Duration.fromString(time);
		Profile profile = Profile.getByUuid(target);

		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		if (rank == null) {
			sender.sendMessage(Locale.RANK_NOT_FOUND.format());
			return;
		}

		if (duration.getValue() == -1) {
			sender.sendMessage(CC.RED + "That duration is not valid.");
			sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
			return;
		}

		if (!sender.hasPermission("gravity.command.grant." + rank.getDisplayName())) {
			sender.sendMessage(CC.translate("&cNo permission to grant them"));
			return;
		}

		UUID addedBy = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
		Grant grant = new Grant(UUID.randomUUID(), rank, addedBy, System.currentTimeMillis(), reason,
				duration.getValue());

		profile.getGrants().add(grant);
		profile.save();
		profile.activateNextGrant();

		try {
			new DiscordLogger().logGrantAdd(UniqueIDCache.name(target), grant);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new GrantAddPacket(target, grant).send();

		sender.sendMessage(CC.GREEN + "You applied a `{rank}` grant to `{player}` for {time-remaining}."
				.replace("{rank}", rank.getDisplayName())
				.replace("{player}", profile.getUsername())
				.replace("{time-remaining}", grant.getTimeRemaining()));

	}
}