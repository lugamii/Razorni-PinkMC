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

public class KickCommand {

	@Command(names = "kick", permission = "gravity.command.kick", async = true)
	public static void kick(CommandSender sender, @Param(name = "target") Profile profile, @Param(name = "reason", wildcard = true) String reason) {
		if (sender instanceof Player) {
			if ((Profile.getProfiles().get(((Player)sender).getUniqueId())).getActiveRank().getWeight() < profile.getActiveRank().getWeight()) {
				sender.sendMessage(CC.translate("&cYou cannot ban someone with higher priority than you."));
				return;
			}
		}
		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.KICK, System.currentTimeMillis(),
				reason, -1);

		if (sender instanceof Player) {
			punishment.setAddedBy(((Player) sender).getUniqueId());
		}

		profile.getPunishments().add(punishment);
		profile.save();
		new PunishmentAddPacket(punishment, profile, staffName, profile.getColoredUsername(), true, false).send();

		if (sender instanceof Player) {
			Profile p = Profile.getByUuid(((Player) sender).getUniqueId());

			p.getStaffInfo().setKicks(p.getStaffInfo().getKicks() + 1);
			p.getStaffInfo().getPunishments().add(punishment);
			p.save();
		}

//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				if (profile.getPlayer() != null) {
//					profile.getPlayer().sendMessage(punishment.getKickMessage());
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							profile.getPlayer().kickPlayer(punishment.getKickMessage());
//						}
//					}.runTaskLater(Core.getInstance(), 10);
//				}
//			}
//		}.runTask(Core.getInstance());
	}
}
