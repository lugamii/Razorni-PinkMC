package dev.razorni.core.database.redis.packets.punish;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.Objects;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 4:04 PM
 * Core / rip.orbit.gravity.database.redis.packets.punish
 */

@AllArgsConstructor
@Data
public class PunishmentResolvePacket implements XPacket {

	private Punishment punishment;
	private UUID sender;
	private Profile target;
	private String reason;
	private String senderDisplay;
	private String targetDisplay;
	private boolean silent;

	@Override
	public void onReceive() {

		punishment.setResolvedBy(sender);
		punishment.setResolved(true);
		punishment.setResolvedReason(reason);
		punishment.setResolvedAt(System.currentTimeMillis());

		punishment.broadcast(senderDisplay, targetDisplay, silent);

		target.getPunishments().removeIf(other -> Objects.equals(other, punishment));
		target.getPunishments().add(punishment);

		target.save();

	}

	@Override
	public String getID() {
		return "Punishment Resolve";
	}
}
