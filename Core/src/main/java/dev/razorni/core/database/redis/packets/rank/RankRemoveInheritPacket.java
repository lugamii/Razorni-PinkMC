package dev.razorni.core.database.redis.packets.rank;

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
public class RankRemoveInheritPacket implements XPacket {

	private Rank rank;
	private Rank inherit;

	@Override
	public void onReceive() {
		String name = rank.getDisplayName();
		String inheritName = inherit.getDisplayName();
		if (rank != null) {
			rank.getInherited().remove(inherit);

			for (Player player : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setupBukkitPlayer(player);
			}

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + name + " has just been removed an inheritance &6(Global Update) &7&o(" + inheritName + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + name + " has just been removed an inheritance &6(Global Update) &7&o(" + inheritName + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank Remove Inherit";
	}
}
