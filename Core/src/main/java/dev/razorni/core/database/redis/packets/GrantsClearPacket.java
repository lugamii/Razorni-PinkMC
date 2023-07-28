package dev.razorni.core.database.redis.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 5:35 PM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class GrantsClearPacket implements XPacket {

	private Profile target;

	@Override
	public void onReceive() {
		target.getGrants().clear();
		target.checkGrants();
		target.save();

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Grants Clear";
	}
}
