package dev.razorni.core.profile.permission;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonDeserializer;

public class TimedPermissionJsonDeserializer implements JsonDeserializer<TimedPermission> {

	@Override
	public TimedPermission deserialize(JsonObject object) {

		return new TimedPermission(
				object.get("permission").getAsString(),
				object.get("addedAt").getAsLong(),
				object.get("duration").getAsLong()
		);
	}

}
