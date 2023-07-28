package dev.razorni.core.extras.reminder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 6:13 PM
 * Core / rip.orbit.gravity.profile.reminder
 */

@Data
@AllArgsConstructor
public class Reminder {

	@Getter private static ReminderJsonDeserializer deserializer = new ReminderJsonDeserializer();
	@Getter private static ReminderJsonSerializer serializer = new ReminderJsonSerializer();

	private String title;
	private long sentAt;
	private String message;

	public String getSentDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("AEST"));
		return simpleDateFormat.format(new Date(sentAt));
	}
}
