package dev.razorni.core.database.redis.packets.rank;

import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:44 PM
 * Core / rip.orbit.gravity.database.redis.packets.rank
 */

@AllArgsConstructor
@Data
public class RankSetWeightPacket implements XPacket {

	private Rank rank;
	private int newWeight;

	@Override
	public void onReceive() {
		String name = rank.getDisplayName();
		if (rank != null) {
			rank.setWeight(newWeight);

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + name + " has just been reweighted &6(Global Update) &7&o(" + newWeight + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + name + " has just been reweighted &6(Global Update) &7&o(" + newWeight + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank Set Weight";
	}
}
