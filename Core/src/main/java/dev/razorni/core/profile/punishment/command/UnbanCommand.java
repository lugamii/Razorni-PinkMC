package dev.razorni.core.profile.punishment.command;
import dev.razorni.core.database.redis.packets.punish.PunishmentResolvePacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class UnbanCommand {

	@Command(names = "unban", permission = "gravity.command.unban", async = true)
	public static void unban(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "reason", wildcard = true) String reason) {

		Profile profile = Profile.getByUuid(target);

		if (profile.getActiveBan() == null) {
			sender.sendMessage(CC.RED + "That player is not banned!");
			return;
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender).getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = profile.getActiveBan();
		punishment.setResolvedAt(System.currentTimeMillis());
		punishment.setResolvedReason(reason);
		punishment.setResolved(true);
		punishment.setResolvedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

		if (sender instanceof Player) {
			Profile p = Profile.getByUuid(((Player) sender).getUniqueId());
			p.getStaffInfo().setPunishmentResolved(p.getStaffInfo().getPunishmentResolved() + 1);
			p.save();
		}

//		new PunishmentRemovePacket(punishment, profile, staffName, profile.getColoredUsername(), true, false).send();
		new PunishmentResolvePacket(punishment, punishment.getResolvedBy(), profile, reason, staffName, profile.getColoredUsername(), true).send();

		profile.getPunishments().removeIf(other -> Objects.equals(other, punishment));
		profile.getPunishments().add(punishment);

		profile.save();

//		JsonBuilder builder = new JsonBuilder();
//		builder.addProperty("uuid", punishment.getUuid().toString());
//		builder.addProperty("target", profile.getColoredUsername());
//		builder.addProperty("targetUUID", profile.getUuid().toString());
//		builder.addProperty("silent", true);
//		builder.addProperty("undo", true);
//		builder.addProperty("type", "BAN");
//		builder.addProperty("resolvedBy", (punishment.getResolvedBy() == null ? "null" : punishment.getResolvedBy().toString()));
//		builder.addProperty("resolvedAt", punishment.getResolvedAt());
//		builder.addProperty("resolvedReason", punishment.getResolvedReason());
//		builder.addProperty("addedBy", staffName);
//		builder.addProperty("addedAt", punishment.getAddedAt());
//		builder.addProperty("addedReason", punishment.getAddedReason());
//		builder.addProperty("duration", punishment.getDuration());
//
//		new PunishPacket(builder).send();

//		Core.getInstance().getPacketBase().sendPacket(new PacketBroadcastPunishment(punishment, staffName, profile.getColoredUsername(), profile.getUuid(), true));
	}
}
