package dev.razorni.core.profile.punishment.command;
import dev.razorni.core.database.redis.packets.punish.PunishmentAddPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.duration.Duration;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;


import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand {

	@Command(names = "mute", permission = "gravity.command.mute", async = true)
	public static void mute(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "time", defaultValue = "perm") String time, @Param(name = "reason", wildcard = true) String reason) {

		Profile profile = Profile.getByUuid(target);
		Duration duration = Duration.fromString(time);

		if (profile.getActiveMute() != null) {
			sender.sendMessage(CC.RED + "That player is already muted.");
			return;
		}

		if (duration.getValue() == -1) {
			sender.sendMessage(CC.RED + "That duration is not valid.");
			sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
			return;
		}

		if (sender instanceof Player) {
			if ((Profile.getProfiles().get(((Player)sender).getUniqueId())).getActiveRank().getWeight() < profile.getActiveRank().getWeight()) {
				sender.sendMessage(CC.translate("&cYou cannot ban someone with higher priority than you."));
				return;
			}
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.MUTE, System.currentTimeMillis(),
				reason, duration.getValue());



		if (sender instanceof Player) {
			punishment.setAddedBy(((Player) sender).getUniqueId());
		}

		profile.getPunishments().add(punishment);
		profile.save();

		Player player = profile.getPlayer();

		if (player != null) {
			String senderName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender).getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";
			player.sendMessage(CC.RED + "You have been muted by " + senderName + CC.RED + " for: " + CC.YELLOW + reason);

			if (!punishment.isPermanent()) {
				player.sendMessage(CC.RED + "This mute will expire in " + CC.YELLOW + punishment.getTimeRemaining());
			}
		}
		profile.getPunishments().add(punishment);
		profile.save();
		new PunishmentAddPacket(punishment, profile, staffName, profile.getColoredUsername(), true, false).send();

//		JsonBuilder builder = new JsonBuilder();
//		builder.addProperty("uuid", punishment.getUuid().toString());
//		builder.addProperty("target", profile.getColoredUsername());
//		builder.addProperty("targetUUID", profile.getUuid().toString());
//		builder.addProperty("silent", true);
//		builder.addProperty("undo", false);
//		builder.addProperty("type", "MUTE");
//		builder.addProperty("addedBy", staffName);
//		builder.addProperty("addedAt", punishment.getAddedAt());
//		builder.addProperty("addedReason", punishment.getAddedReason());
//		builder.addProperty("duration", punishment.getDuration());
//
//		new PunishPacket(builder).send();

//		Core.getInstance().getPacketBase().sendPacket(new PacketBroadcastPunishment(punishment, staffName, profile.getColoredUsername(), profile.getUuid(), true));


		if (sender instanceof Player) {
			Profile p = Profile.getByUuid(((Player) sender).getUniqueId());

			p.getStaffInfo().setMutes(p.getStaffInfo().getMutes() + 1);
			p.getStaffInfo().getPunishments().add(punishment);
			p.save();
		}
	}

}
