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
public class RankDeletePacket implements XPacket {

	private Rank rank;

	@Override
	public void onReceive() {

		if (rank != null) {
			Rank.getRanks().remove(rank.getUuid());

			for (Profile p : Profile.getProfiles().values()) {
				if (p.getActiveRank().getUuid() == rank.getUuid()) {
					p.checkGrants();
				}
			}

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been deleted &6(Global Update)"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been deleted &6(Global Update)"));
		}
	}

	@Override
	public String getID() {
		return "Rank Delete";
	}
}
