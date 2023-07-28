package dev.razorni.core.extras.reminder;

import com.google.gson.JsonObject;
import dev.razorni.core.util.json.JsonSerializer;

public class ReminderJsonSerializer implements JsonSerializer<Reminder> {

	@Override
	public JsonObject serialize(Reminder reminder) {
		JsonObject object = new JsonObject();
		object.addProperty("title", reminder.getTitle());
		object.addProperty("sentAt", reminder.getSentAt());
		object.addProperty("message", reminder.getMessage());
		return object;
	}

}
