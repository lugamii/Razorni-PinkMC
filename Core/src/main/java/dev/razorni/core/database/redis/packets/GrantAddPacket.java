package dev.razorni.core.database.redis.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.grant.event.GrantAppliedEvent;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.Objects;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/08/2021 / 3:49 AM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class GrantAddPacket implements XPacket {

	private UUID target;
	private Grant grant;

	@Override
	public void onReceive() {
		Profile profile = Profile.getByUuid(this.target);

		Player player = profile.getPlayer();

		profile.getGrants().removeIf(other -> Objects.equals(other, grant));
		profile.getGrants().add(grant);
		profile.activateNextGrant();

		if (player != null) {
			profile.setupBukkitPlayer(player);
			new GrantAppliedEvent(player, grant).call();
		}

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "GrantAdd";
	}
}
