package dev.razorni.core.profile.punishment.command;
import dev.razorni.core.database.redis.packets.punish.PunishmentAddPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;


import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand {

	@Command(names = "warn", permission = "gravity.command.warn", async = true)
	public static void warn(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "reason", wildcard = true) String reason) {

		Profile profile = Profile.getByUuid(target);

		if (sender instanceof Player) {
			if ((Profile.getProfiles().get(((Player)sender).getUniqueId())).getActiveRank().getWeight() < profile.getActiveRank().getWeight()) {
				sender.sendMessage(CC.translate("&cYou cannot ban someone with higher priority than you."));
				return;
			}
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.WARN, System.currentTimeMillis(),
				reason, -1);

		if (sender instanceof Player) {
			punishment.setAddedBy(((Player) sender).getUniqueId());
		}

		profile.getPunishments().add(punishment);
		profile.save();

		Player player = profile.getPlayer();

		if (player != null) {
			String senderName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender).getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";
			player.sendMessage(CC.RED + "You have been warned by " + senderName + CC.RED + " for: " + CC.YELLOW + reason);
		}

		new PunishmentAddPacket(punishment, profile, staffName, profile.getColoredUsername(), true, false).send();

//		JsonBuilder builder = new JsonBuilder();
//		builder.addProperty("uuid", punishment.getUuid().toString());
//		builder.addProperty("target", profile.getColoredUsername());
//		builder.addProperty("targetUUID", profile.getUuid().toString());
//		builder.addProperty("silent", true);
//		builder.addProperty("undo", true);
//		builder.addProperty("type", "WARN");
//		builder.addProperty("addedBy", staffName);
//		builder.addProperty("addedAt", punishment.getAddedAt());
//		builder.addProperty("addedReason", punishment.getAddedReason());
//		builder.addProperty("duration", punishment.getDuration());
//
//		new PunishPacket(builder).send();

//		Core.getInstance().getPacketBase().sendPacket(new PacketBroadcastPunishment(punishment, staffName, profile.getColoredUsername(), profile.getUuid(), true));

		if (sender instanceof Player) {
			Profile p = Profile.getByUuid(((Player) sender).getUniqueId());

			p.getStaffInfo().setWarns(p.getStaffInfo().getWarns() + 1);
			p.getStaffInfo().getPunishments().add(punishment);
			p.save();
		}
	}

}
