package dev.razorni.core.profile.punishment.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.punishment.PunishmentType;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 12:49 PM
 * Core / rip.orbit.gravity.profile.punishment.object
 */

@Data
@AllArgsConstructor
public class Offense {

	private String stage;
	private String banTime;
	private PunishmentType type;

}
