package dev.razorni.core.database.redis.packets.global;

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
public class GlobalCommandPacket implements XPacket {

	private String command;

	@Override
	public void onReceive() {

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Global CMD Packet";
	}
}
