package dev.razorni.core.extras.reminder;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonDeserializer;

public class ReminderJsonDeserializer implements JsonDeserializer<Reminder> {

	@Override
	public Reminder deserialize(JsonObject object) {
		return new Reminder(object.get("title").getAsString(),
				object.get("sentAt").getAsLong(),
				object.get("message").getAsString()
				);
	}

}
