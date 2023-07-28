package dev.razorni.core.profile.permission;

import dev.razorni.core.profile.punishment.Punishment;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 17/08/2021 / 6:25 PM
 * Core / rip.orbit.gravity.profile.permission
 */

@AllArgsConstructor
@Data
public class TimedPermission {

	public static TimedPermissionJsonDeserializer deserializer = new TimedPermissionJsonDeserializer();
	public static TimedPermissionJsonSerializer serializer = new TimedPermissionJsonSerializer();

	private String permission;
	private long addedAt;
	private long duration;

	public long getRemaining() {
		return duration + addedAt - System.currentTimeMillis();
	}

	public String getTimeRemaining() {
		if (duration == Integer.MAX_VALUE) {
			return "Forever";
		}

		return Punishment.TimeUtils.formatLongIntoDetailedString(getRemaining() / 1000L);
	}

}
