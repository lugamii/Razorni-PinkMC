package dev.razorni.core.profile.punishment;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonSerializer;

public class PunishmentJsonSerializer implements JsonSerializer<Punishment> {

	@Override
	public JsonObject serialize(Punishment punishment) {
		JsonObject object = new JsonObject();
		object.addProperty("uuid", punishment.getUuid().toString());
		object.addProperty("type", punishment.getType().name());
		object.addProperty("addedBy", punishment.getAddedBy() == null ? null : punishment.getAddedBy().toString());
		object.addProperty("addedAt", punishment.getAddedAt());
		object.addProperty("addedReason", punishment.getAddedReason());
		object.addProperty("duration", punishment.getDuration());
		object.addProperty("pardonedBy", punishment.getResolvedBy() == null ? null : punishment.getResolvedBy().toString());
		object.addProperty("pardonedAt", punishment.getResolvedAt());
		object.addProperty("pardonedReason", punishment.getResolvedReason());
		object.addProperty("pardoned", punishment.isResolved());
		return object;
	}

}
