package dev.razorni.core.extras.friends.command;


import dev.razorni.core.database.redis.packets.friend.FriendAddPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 7:46 PM
 * Core / rip.orbit.gravity.profile.friends.command
 */

public class FriendAcceptCommand {
	@Command(names = "friend accept", permission = "")
	public static void add(Player sender, @Param(name = "target")UUID uuid) {

		Profile target = Profile.getByUuid(uuid);
		Profile senderProfile = Profile.getByUuid(sender.getUniqueId());

		if (!senderProfile.getFriendRequests().contains(senderProfile.friendRequestByName(target.getUuid()))) {
			sender.sendMessage(CC.translate("&cThere's no pending friend request regarding that player."));
			return;
		}

		new FriendAddPacket(sender.getUniqueId(), target.getUuid()).send();

		sender.sendMessage(CC.translate("&eYou have successfully accepted &d" + target.getDisplayName() + "'s &efriend request."));

	}

}
