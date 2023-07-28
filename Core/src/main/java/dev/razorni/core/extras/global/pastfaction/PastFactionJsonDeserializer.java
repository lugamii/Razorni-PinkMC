package dev.razorni.core.extras.global.pastfaction;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonDeserializer;

public class PastFactionJsonDeserializer implements JsonDeserializer<PastFaction> {

	@Override
	public PastFaction deserialize(JsonObject object) {
		return new PastFaction(
				object.get("name").getAsString());
	}

}
