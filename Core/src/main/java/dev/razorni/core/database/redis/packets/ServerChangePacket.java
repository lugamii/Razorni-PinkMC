package dev.razorni.core.database.redis.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 5:19 PM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class ServerChangePacket implements XPacket {

	private String sender;
	private String server;
	private boolean offline;

	@Override
	public void onReceive() {
		Profile profile = Profile.getByUuid(UUID.fromString(sender));

		if (profile != null) {
			profile.setServerOn(server);
			profile.setOnline(!offline);
		}
	}

	@Override
	public String getID() {
		return "Server Change (Update Status)";
	}
}
