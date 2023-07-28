package dev.razorni.core.database.redis.packets.rank;

import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:44 PM
 * Core / rip.orbit.gravity.database.redis.packets.rank
 */

@AllArgsConstructor
@Data
public class RankSetSuffixPacket implements XPacket {

	private Rank rank;
	private String newSuffix;

	@Override
	public void onReceive() {
		String name = rank.getDisplayName();
		if (rank != null) {
			rank.setSuffix(newSuffix);

			for (Profile p : Profile.getProfiles().values()) {
				if (p.getActiveRank().getDisplayName().equals(rank.getDisplayName())) {
					p.getPlayer().setDisplayName(rank.getPrefix() + p.getUsername() + rank.getSuffix());
				}
			}

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + name + " has just been resuffixed &6(Global Update) &7&o(" + newSuffix + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + name + " has just been resuffixed &6(Global Update) &7&o(" + newSuffix + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank SetSuffix";
	}
}
