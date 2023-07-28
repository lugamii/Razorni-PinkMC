package dev.razorni.core.profile.option.staff;

import dev.razorni.core.profile.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 2:05 PM
 * Core / rip.orbit.gravity.profile.option
 */
@Getter
public class StaffInfo {

	@Setter
	private int bans = 0;

	@Setter
	private int kicks = 0;

	@Setter
	private int blacklists = 0;

	@Setter
	private int mutes = 0;

	@Setter
	private int warns = 0;

	@Setter
	private int reportsResolved = 0;

	@Setter
	private int punishmentResolved = 0;

	@Setter
	private int strikes = 0;

	@Setter private List<Punishment> punishments;

	public StaffInfo() {
		punishments = new ArrayList<>();
	}

}
