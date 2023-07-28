package dev.razorni.core.database.redis.packets.global;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:55 PM
 * Core / rip.orbit.gravity.database.redis.packets.punish
 */

@AllArgsConstructor
@Data
public class PunishmentsClearPacket implements XPacket {

	private boolean clearAll;
	private Profile target;

	@Override
	public void onReceive() {
		if (clearAll) {
			for (Profile profile : Profile.getProfiles().values()) {
				profile.getPunishments().clear();

				Bukkit.getConsoleSender().sendMessage(CC.translate("&cSuccessfully cleared punishments for " + profile.getColoredUsername()));
			}
		} else {
			target.getPunishments().clear();
			target.save();
		}
	}

	@Override
	public String getID() {
		return "Punishment Remove";
	}
}
