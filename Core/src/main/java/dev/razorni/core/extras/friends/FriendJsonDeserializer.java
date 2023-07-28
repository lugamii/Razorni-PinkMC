package dev.razorni.core.extras.friends;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonDeserializer;

import java.util.UUID;

public class FriendJsonDeserializer implements JsonDeserializer<Friend> {

	@Override
	public Friend deserialize(JsonObject object) {
		return new Friend(UUID.fromString(object.get("uuid").getAsString())
				);
	}

}
