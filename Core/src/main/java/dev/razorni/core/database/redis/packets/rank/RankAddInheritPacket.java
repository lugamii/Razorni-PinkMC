package dev.razorni.core.database.redis.packets.rank;

import dev.razorni.core.Core;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:44 PM
 * Core / rip.orbit.gravity.database.redis.packets.rank
 */

@AllArgsConstructor
@Data
public class RankAddInheritPacket implements XPacket {

	private Rank rank;
	private Rank inherit;

	@Override
	public void onReceive() {
		if (rank != null) {
			if (!rank.getInherited().contains(inherit)) {
				rank.getInherited().add(inherit);
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setupPermissionsAttachment(Core.getInstance(), player);
			}
			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been added an inheritance &6(Global Update) &7&o(" + inherit.getDisplayName() + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been added an inheritance &6(Global Update) &7&o(" + inherit.getDisplayName() + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank Add Inherit";
	}
}
