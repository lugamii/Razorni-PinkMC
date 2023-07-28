package dev.razorni.core.database.redis.packets.global;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 5:16 PM
 * Core / rip.orbit.gravity.database.redis.packets.global
 */

@AllArgsConstructor
@Data
public class ServerRebootPacket implements XPacket {

	private String server;
	private String status;

	@Override
	public void onReceive() {
		Bukkit.getConsoleSender().sendMessage(CC.translate("&9[Monitor] &7" + server + " &bjust became " + status));
		Bukkit.broadcast(CC.translate("&9[Monitor] &7" + server + " &bjust became " + status), "gravity.staff");

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Server Reboot";
	}
}
