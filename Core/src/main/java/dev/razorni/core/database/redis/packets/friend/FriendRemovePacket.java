package dev.razorni.core.database.redis.packets.friend;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:26 PM
 * Core / rip.orbit.gravity.database.redis.packets.friend
 */

@AllArgsConstructor
@Data
public class FriendRemovePacket implements XPacket {

	private Profile sender;
	private Profile target;

	@Override
	public void onReceive() {

		target.getFriends().remove(target.friendByName(sender.getUuid()));
		target.save();

		sender.getFriends().remove(sender.friendByName(target.getUuid()));
		sender.save();

		if (target.getPlayer() != null) {
			target.getPlayer().sendMessage(CC.translate("&c" + target.getUsername() + " has removed you as a friend."));
		}

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Friend Remove";
	}
}
