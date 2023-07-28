package dev.razorni.core.profile.permission;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonSerializer;

public class TimedPermissionJsonSerializer implements JsonSerializer<TimedPermission> {

	@Override
	public JsonObject serialize(TimedPermission timedPermission) {
		JsonObject object = new JsonObject();
		object.addProperty("permission", timedPermission.getPermission());
		object.addProperty("addedAt", timedPermission.getAddedAt());
		object.addProperty("duration", timedPermission.getDuration());
		return object;
	}

}
