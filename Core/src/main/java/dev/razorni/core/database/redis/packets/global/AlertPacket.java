package dev.razorni.core.database.redis.packets.global;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/09/2021 / 7:05 PM
 * Core / rip.orbit.gravity.database.redis.packets.global
 */

@AllArgsConstructor
@Data
public class AlertPacket implements XPacket {

	private String message;

	@Override
	public void onReceive() {
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(CC.translate(message));
		Bukkit.broadcastMessage(" ");

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "AlertPacket";
	}
}
