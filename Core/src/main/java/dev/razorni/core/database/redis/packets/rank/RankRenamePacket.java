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
public class RankRenamePacket implements XPacket {

	private Rank rank;
	private String newName;

	@Override
	public void onReceive() {
		String name = rank.getDisplayName();
		if (rank != null) {
			rank.setDisplayName(newName);

			for (Player player : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setupBukkitPlayer(player);
			}

			Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + name + " has just been renamed &6(Global Update) &7&o(" + newName + ")"), "gravity.staff");
			Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + name + " has just been renamed &6(Global Update) &7&o(" + newName + ")"));
		}
	}

	@Override
	public String getID() {
		return "Rank Rename";
	}
}
