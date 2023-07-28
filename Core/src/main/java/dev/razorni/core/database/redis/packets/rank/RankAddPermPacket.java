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
public class RankAddPermPacket implements XPacket {

	private Rank toLook;
	private String permission;

	@Override
	public void onReceive() {
		Rank rank = Rank.getRankByUuid(toLook.getUuid());
		rank.addPermission(permission);

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setupBukkitPlayer(player);
		}

		Bukkit.broadcast(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been added a permission &6(Global Update) &7&o(" + permission + ")"), "gravity.staff");
		Bukkit.getConsoleSender().sendMessage(CC.translate("&6&lRank Update &f» &7" + rank.getDisplayName() + " has just been added a permission &6(Global Update) &7&o(" + permission + ")"));

	}

	@Override
	public String getID() {
		return "Rank AddPerm";
	}
}
