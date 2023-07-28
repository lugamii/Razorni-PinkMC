package dev.razorni.core.database.redis.packets.friend;

import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.friends.Friend;
import dev.razorni.core.extras.friends.request.FriendRequest;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:26 PM
 * Core / rip.orbit.gravity.database.redis.packets.friend
 */

@AllArgsConstructor
@Data
public class FriendAddPacket implements XPacket {

	private UUID sender;
	private UUID target;

	@Override
	public void onReceive() {
		Profile sender = Profile.getByUuid(this.sender);
		Profile target = Profile.getByUuid(this.target);
		target.getFriendRequests().remove(new FriendRequest(sender.getUuid(), target.getUuid()));
		target.getFriendRequests().remove(new FriendRequest(target.getUuid(), sender.getUuid()));

		sender.getFriendRequests().remove(new FriendRequest(sender.getUuid(), target.getUuid()));
		sender.getFriendRequests().remove(new FriendRequest(target.getUuid(), sender.getUuid()));

		target.getFriends().add(new Friend(sender.getUuid()));
		sender.getFriends().add(new Friend(target.getUuid()));

		sender.save();
		target.save();

		if (target.getPlayer() != null) {
			target.getPlayer().sendMessage(CC.translate("&d" + sender.getUsername() + "&e has just accepted your friend request."));
		}

	}

	@Override
	public String getID() {
		return "Friend Add";
	}
}
