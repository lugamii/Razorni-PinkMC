package dev.razorni.core.database.redis.packets.punish;

import dev.razorni.core.Core;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 4:04 PM
 * Core / rip.orbit.gravity.database.redis.packets.punish
 */

@AllArgsConstructor
@Data
public class PunishmentAddPacket implements XPacket {

	private Punishment punishment;
	private Profile target;
	private String senderDisplay;
	private String targetName;
	private boolean silent;
	private boolean clear;

	@Override
	public void onReceive() {

		target.getPunishments().add(punishment);

		punishment.broadcast(senderDisplay, targetName, silent);

		Player player = target.getPlayer();

		if (player != null) {

			if (clear) {
				player.getInventory().clear();
				player.updateInventory();
			}

			if (punishment.getType() == PunishmentType.KICK) {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.sendMessage(punishment.getKickMessage());
						player.kickPlayer(punishment.getKickMessage());
					}
				}.runTask(Core.getInstance());
			}
			if (punishment.getType().isBan()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.sendMessage(punishment.getKickMessage());
						player.kickPlayer(punishment.getKickMessage());
					}
				}.runTask(Core.getInstance());
			}

			if (punishment.getType() == PunishmentType.BLACKLIST) {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.sendMessage(punishment.getKickMessage());
						player.kickPlayer(punishment.getKickMessage());
					}
				}.runTask(Core.getInstance());
			}
		}
	}

	@Override
	public String getID() {
		return "Punishment Add";
	}
}
