package dev.razorni.core.extras.reminder.command;
import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.ReminderUpdatePacket;
import dev.razorni.core.extras.reminder.Reminder;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 6:31 PM
 * Core / rip.orbit.gravity.profile.reminder.command
 */

public class ReminderCommand {

	@Command(names = {"sendglobalreminder", "sendreminder", "remindall"}, permission = "op")
	public static void sendglobalreminder(CommandSender sender, @Param(name = "title") String title, @Param(name = "message", wildcard = true) String message) {
		if (Core.getInstance().getReminderHandler().getReminderByName(title) != null) {
			sender.sendMessage(CC.translate("&cThat reminder is already existing."));
			return;
		}
		Reminder reminder = new Reminder(title, System.currentTimeMillis(), message);

//		JsonBuilder builder = new JsonBuilder();
//		builder.addProperty("title", title);
//		builder.addProperty("message", message);
//		builder.addProperty("sentAt", System.currentTimeMillis());
//		builder.addProperty("remove", false);
//		builder.addProperty("announce", true);

//		new ReminderUpdatePacket(builder).send();
		new ReminderUpdatePacket(reminder, false, true).send();

		sender.sendMessage(CC.translate("&aYou have just sent the whole network a reminder."));
	}

	@Command(names = {"deleteglobalreminder", "deletereminder", "deleteremind"}, permission = "op")
	public static void deleteglobalreminder(CommandSender sender, @Param(name = "title") String title) {
		if (Core.getInstance().getReminderHandler().getReminderByName(title) == null) {
			sender.sendMessage(CC.translate("&cThat reminder does not exist."));
			return;
		}
		Reminder reminder = Core.getInstance().getReminderHandler().getReminderByName(title);

//		JsonBuilder builder = new JsonBuilder();
//		builder.addProperty("title", title);
//		builder.addProperty("message", reminder.getMessage());
//		builder.addProperty("sentAt", System.currentTimeMillis());
//		builder.addProperty("remove", true);
//
//		new ReminderUpdatePacket(builder).send();

		new ReminderUpdatePacket(reminder, true, false).send();

//		builder.addProperty("announce", false);

		sender.sendMessage(CC.translate("&aYou have just deleted the " + title + " reminder."));
	}

}
