package dev.razorni.core.extras.friends.request;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonDeserializer;

import java.util.UUID;

public class FriendRequestJsonDeserializer implements JsonDeserializer<FriendRequest> {

	@Override
	public FriendRequest deserialize(JsonObject object) {
		return new FriendRequest(UUID.fromString(object.get("sender").getAsString()), UUID.fromString(object.get("target").getAsString()));
	}

}
