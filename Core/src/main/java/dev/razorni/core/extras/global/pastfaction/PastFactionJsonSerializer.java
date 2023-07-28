package dev.razorni.core.extras.global.pastfaction;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonSerializer;

public class PastFactionJsonSerializer implements JsonSerializer<PastFaction> {

	@Override
	public JsonObject serialize(PastFaction pastFaction) {
		JsonObject object = new JsonObject();
		object.addProperty("name", pastFaction.getFaction());
		return object;
	}

}
