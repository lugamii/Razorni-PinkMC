package dev.razorni.core.extras.reminder;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.ReminderUpdatePacket;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 12/08/2021 / 3:55 PM
 * Core / rip.orbit.gravity.profile.reminder
 */
public class ReminderHandler {

	private final Core plugin = Core.getInstance();

	@Getter
	private final MongoCollection<Document> reminderCollection;

	@Getter
	private final List<Reminder> reminders;

	public ReminderHandler() {
		this.reminders = new ArrayList<>();
		this.reminderCollection = plugin.getMongoHandler().getMongoDatabase().getCollection("reminders");
		loadReminders();
	}

	public Reminder getReminderByName(String search) {
		return this.reminders.stream().filter(reminder -> reminder.getTitle().equalsIgnoreCase(search)).findFirst().orElse(null);
	}

	private void loadReminders() {
		for (Document document : reminderCollection.find()) {
			Reminder reminder = new Reminder(document.getString("title"), document.getLong("sentAt"), document.getString("message"));
			saveReminder(reminder, false);
		}
	}

	public Optional<Document> getReminderFromDB(String name){
		return Optional.ofNullable(reminderCollection.find(Filters.eq("title", name)).first());
	}

	public void loadReminderByName(String name) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			getReminderFromDB(name).ifPresent(document -> {
				Reminder reminder = getReminderByName(name);

				if (reminder != null) {
					reminder.setTitle(document.getString("title"));
					reminder.setMessage(document.getString("message"));
					reminder.setSentAt(document.getLong("sentAt"));
				} else {
					reminder = new Reminder(document.getString("title"), document.getLong("sentAt"), document.getString("message"));
					reminders.remove(reminder);
					reminders.add(reminder);
				}
			});
		});
	}

	public void saveReminder(Reminder reminder) {
		if (!reminders.contains(reminder)) {
			reminders.add(reminder);
		}
		Document document = new Document();
		document.put("title", reminder.getTitle());
		document.put("message", reminder.getMessage());
		document.put("sentAt", reminder.getSentAt());

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			reminderCollection.replaceOne(Filters.eq("title", reminder.getTitle()), document, new ReplaceOptions().upsert(true));
//			Core.getInstance().getPacketBase().sendPacket(new PacketUpdateReminder(reminder.getTitle(),
//					Core.getInstance().getConfig().getString("SERVER_NAME"),
//					false
//			));

//			JsonBuilder builder = new JsonBuilder();
//			builder.addProperty("title", reminder.getTitle());
//			builder.addProperty("message", reminder.getMessage());
//			builder.addProperty("sentAt", reminder.getSentAt());
//			builder.addProperty("remove", false);
//			builder.addProperty("announce", false);
//
//			new ReminderUpdatePacket(builder).send();

			new ReminderUpdatePacket(reminder, false, false).send();

		});
	}

	public void saveReminder(Reminder reminder, boolean global) {
		if (!reminders.contains(reminder)) {
			reminders.add(reminder);
		}
		Document document = new Document();
		document.put("title", reminder.getTitle());
		document.put("message", reminder.getMessage());
		document.put("sentAt", reminder.getSentAt());

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			reminderCollection.replaceOne(Filters.eq("title", reminder.getTitle()), document, new ReplaceOptions().upsert(true));
			if (global) {
//				JsonBuilder builder = new JsonBuilder();
//				builder.addProperty("title", reminder.getTitle());
//				builder.addProperty("message", reminder.getMessage());
//				builder.addProperty("sentAt", reminder.getSentAt());
//				builder.addProperty("remove", false);
//				builder.addProperty("announce", false);
//
//				new ReminderUpdatePacket(builder).send();

				new ReminderUpdatePacket(reminder, false, false).send();
			}

		});
	}

	public void deleteReminder(Reminder reminder) {
		reminders.remove(reminder);
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			reminderCollection.deleteOne(Filters.eq("title", reminder.getTitle())); // Deletes the reminder
//			JsonBuilder builder = new JsonBuilder();
//			builder.addProperty("title", reminder.getTitle());
//			builder.addProperty("message", reminder.getMessage());
//			builder.addProperty("sentAt", reminder.getSentAt());
//			builder.addProperty("remove", true);
//			builder.addProperty("announce", false);
//
//			new ReminderUpdatePacket(builder).send();
			new ReminderUpdatePacket(reminder, true, false).send();
		});
	}
}
