package dev.razorni.core.extras.friends;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonSerializer;

public class FriendJsonSerializer implements JsonSerializer<Friend> {

	@Override
	public JsonObject serialize(Friend friend) {
		JsonObject object = new JsonObject();
		object.addProperty("uuid", friend.getFriend().toString());
		return object;
	}

}
