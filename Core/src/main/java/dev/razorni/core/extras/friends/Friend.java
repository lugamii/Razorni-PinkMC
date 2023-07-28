package dev.razorni.core.extras.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 7:32 PM
 * Core / rip.orbit.gravity.profile.friends
 */

@AllArgsConstructor
@Data
public class Friend {

	@Getter private static FriendJsonDeserializer deserializer = new FriendJsonDeserializer();
	@Getter private static FriendJsonSerializer serializer = new FriendJsonSerializer();

	private UUID friend;

}
