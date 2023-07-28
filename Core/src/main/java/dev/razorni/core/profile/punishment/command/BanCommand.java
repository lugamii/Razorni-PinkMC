package dev.razorni.core.profile.punishment.command;
import dev.razorni.core.util.Locale;
import dev.razorni.core.database.redis.packets.punish.PunishmentAddPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.TimeUtil;
import dev.razorni.core.util.duration.Duration;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Flag;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;


import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand {

	@Command(names = {"ban", "tban","tempban"}, permission = "gravity.command.ban", async = true)
	public static void tempBan(CommandSender sender, @Flag(value = {"c", "clear"}, description = "Clear the player's inventory") boolean clear, @Flag(value = {"p", "public"}, description = "Publicize the punishment") boolean broadcast, @Param(name = "target") UUID target, @Param(name = "time", defaultValue = "perm") String time, @Param(name = "reason", wildcard = true) String reason) {
		ban(sender, clear, broadcast, target, time, reason);
	}


	private static void ban(CommandSender sender, boolean clear, boolean broadcast, UUID target, String time, String reason) {
		Profile profile = Profile.getByUuid(target);
		Duration duration = Duration.fromString(time);
		if (profile != null && profile.isLoaded()) {

			if (profile.getActiveBan() != null) {
				sender.sendMessage(CC.RED + "That player is already banned.");
			} else if (duration.getValue() <= -1L) {
				sender.sendMessage(CC.RED + "That duration is not valid.");
				sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
			} else if (!sender.hasPermission("gravity.command.ban.permanent") && duration.getValue() > TimeUtil.parseTime("7d")) {
				sender.sendMessage(CC.translate("&cYou cannot ban for more than 7 days."));
			} else {
				if (sender instanceof Player) {
					if ((Profile.getProfiles().get(((Player)sender).getUniqueId())).getActiveRank().getWeight() < profile.getActiveRank().getWeight()) {
						sender.sendMessage(CC.translate("&cYou cannot ban someone with higher priority than you."));
						return;
					}
				}
				String staffName = sender instanceof Player ? (Profile.getProfiles().get(((Player)sender).getUniqueId())).getColoredUsername() : CC.DARK_RED + "Console";
				Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BAN, System.currentTimeMillis(), reason, duration.getValue());
				if (sender instanceof Player) {
					punishment.setAddedBy(((Player)sender).getUniqueId());
				}

				profile.getPunishments().add(punishment);
				profile.save();

				new PunishmentAddPacket(punishment, profile, staffName, profile.getColoredUsername(), !broadcast, clear).send();

				if (sender instanceof Player) {
					Profile p = Profile.getByUuid(((Player)sender).getUniqueId());
					p.getStaffInfo().setBans(p.getStaffInfo().getBans() + 1);
					p.getStaffInfo().getPunishments().add(punishment);
					p.save();
				}

			}
		} else {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
		}
	}
}
