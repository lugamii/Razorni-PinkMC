package dev.razorni.core.profile.punishment.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:46 AM
 * Core / rip.orbit.gravity.profile.punishment.object
 */

@Data
@AllArgsConstructor
public class PunishReason {

	private String title;
	private List<Offense> offenses;
	private List<PunishmentType> types;


}
