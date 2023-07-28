package dev.razorni.core.database.redis.packets.rank;

import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:44 PM
 * Core / rip.orbit.gravity.database.redis.packets.rank
 */

@AllArgsConstructor
@Data
public class RankSetColorPacket implements XPacket {

	private Rank rank;
	private ChatColor newColor;

	@Override
	public void onReceive() {
		String name = rank.getDisplayName();
		if (rank != null) {
			rank.setColor(newColor);

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + name + " has just been recolored &6(Global Update) &7&o(" + newColor.name() + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + name + " has just been recolored &6(Global Update) &7&o(" + newColor.name() + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank SetColor";
	}
}
