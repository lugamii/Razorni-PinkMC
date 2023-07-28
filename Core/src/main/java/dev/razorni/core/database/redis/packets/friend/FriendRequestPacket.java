package dev.razorni.core.database.redis.packets.friend;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.util.fanciful.FancyMessage;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.friends.request.FriendRequest;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 3:26 PM
 * Core / rip.orbit.gravity.database.redis.packets.friend
 */

@AllArgsConstructor
@Data
public class FriendRequestPacket implements XPacket {

	private Profile sender;
	private Profile target;

	@Override
	public void onReceive() {
		sender.setFriendCooldown(new Cooldown(30000));

		target.getFriendRequests().add(new FriendRequest(sender.getUuid(), target.getUuid()));

		if (target.getPlayer() != null) {
			target.getPlayer().sendMessage(CC.translate("&eYou have just received a &dfriend request&e from &d" + sender.getUsername() + "&e."));
			FancyMessage message = new FancyMessage();
			message.text(CC.translate("&eClick "));
			message.then().text(CC.translate("&dhere")).tooltip(CC.translate("&eClick to &daccept&e their friend request.")).command("/friend accept " + sender.getUsername());
			message.then().text(CC.translate(" &fto accept this friend request."));
			message.send(target.getPlayer());
			target.getPlayer().sendMessage(CC.CHAT_BAR);
		}

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Friend Request";
	}
}
