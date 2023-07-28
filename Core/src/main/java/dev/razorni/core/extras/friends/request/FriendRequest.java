package dev.razorni.core.extras.friends.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 7:21 PM
 * Core / rip.orbit.gravity.profile.friends
 */

@AllArgsConstructor
@Data
public class FriendRequest {

	@Getter private static FriendRequestJsonDeserializer deserializer = new FriendRequestJsonDeserializer();
	@Getter private static FriendRequestJsonSerializer serializer = new FriendRequestJsonSerializer();

	private UUID sender;
	private UUID target;

}
