package dev.razorni.core.database.redis.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.grant.event.GrantExpireEvent;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.Objects;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/08/2021 / 4:02 AM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class GrantRemovePacket implements XPacket {

	private UUID target;
	private Grant grant;

	@Override
	public void onReceive() {
		Profile profile = Profile.getByUuid(target);
		profile.getGrants().removeIf(other -> Objects.equals(other, grant));
		profile.getGrants().add(grant);
		profile.save();
		profile.activateNextGrant();

		if (Bukkit.getPlayer(target) != null) {

			new GrantExpireEvent(profile.getPlayer(), grant);
		}

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "GrantRemove";
	}
}
