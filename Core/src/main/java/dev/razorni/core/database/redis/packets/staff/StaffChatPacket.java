package dev.razorni.core.database.redis.packets.staff;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 5:14 PM
 * Core / rip.orbit.gravity.database.redis.packets.staff
 */

@AllArgsConstructor
@Data
public class StaffChatPacket implements XPacket {

	private String sender;
	private String message;
	private String server;

	@Override
	public void onReceive() {
		Bukkit.getConsoleSender().sendMessage(CC.translate("&9[S] &b(" + server + ") " + sender + "&7:&f " + message));

		Bukkit.getOnlinePlayers()
				.stream()
				.filter(player -> player.hasPermission("gravity.staff"))
				.forEach(player -> {
					player.sendMessage(CC.translate("&9[S] &b(" + server + ") " + sender + "&7:&f " + message));
				});

	}

	@Override
	public String getID() {
		return null;
	}
}
