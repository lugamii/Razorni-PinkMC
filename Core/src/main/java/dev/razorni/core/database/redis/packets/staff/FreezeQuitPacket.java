package dev.razorni.core.database.redis.packets.staff;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/09/2021 / 7:14 PM
 * Core / rip.orbit.gravity.database.redis.packets.staff
 */

@AllArgsConstructor
@Data
public class FreezeQuitPacket implements XPacket {

	private String target;

	@Override
	public void onReceive() {
		FancyMessage fancyMessage = new FancyMessage();
		fancyMessage.text(CC.translate("&9[F] &7" + target + " &b has left while he was Frozen. &a&oClick here to ban.")).command("/ban -c " + target + " perm Logging Whilst Frozen");

		Bukkit.getOnlinePlayers()
				.stream()
				.filter(player -> player.hasPermission("gravity.staff"))
				.forEach(fancyMessage::send);


		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Freeze Quit";
	}
}
