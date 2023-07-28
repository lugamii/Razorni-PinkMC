package dev.razorni.core.profile.punishment;

import dev.razorni.core.profile.punishment.object.Offense;
import dev.razorni.core.profile.punishment.object.PunishReason;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:56 AM
 * Core / rip.orbit.gravity.profile.punishment
 */

@Getter
public class PunishmentHandler {

	private final List<PunishReason> reasons;

	public PunishmentHandler() {
		reasons = Arrays.asList(
				new PunishReason("Racism", Arrays.asList(
						new Offense("1st", "12 hours", PunishmentType.MUTE),
						new Offense("2nd", "1 day", PunishmentType.MUTE),
						new Offense("3rd", "7 days", PunishmentType.MUTE)),

						Collections.singletonList(PunishmentType.MUTE)
				),
				new PunishReason("Staff/Server Disrespect", Arrays.asList(
						new Offense("1st", "12 hours", PunishmentType.MUTE),
						new Offense("2nd", "1 day", PunishmentType.MUTE),
						new Offense("3rd", "7 days", PunishmentType.MUTE)),

						Collections.singletonList(PunishmentType.MUTE)
				),
				new PunishReason("Excessive Toxicity", Arrays.asList(
						new Offense("1st", "", PunishmentType.WARN),
						new Offense("2nd", "30 minutes", PunishmentType.MUTE),
						new Offense("3rd", "3 hours", PunishmentType.MUTE),
						new Offense("4th", "6 hours", PunishmentType.MUTE),
						new Offense("Other", "6 hours", PunishmentType.MUTE)),

						Arrays.asList(PunishmentType.MUTE, PunishmentType.WARN)
				),
				new PunishReason("Suicidal Encouragement", Arrays.asList(
						new Offense("1st", "3 days", PunishmentType.MUTE),
						new Offense("2nd", "7 days", PunishmentType.MUTE),
						new Offense("3rd", "Permanent", PunishmentType.MUTE)),

						Collections.singletonList(PunishmentType.MUTE)
				),
				new PunishReason("Advertising", Arrays.asList(
						new Offense("1st", "1 day", PunishmentType.MUTE),
						new Offense("2nd", "3 days", PunishmentType.MUTE),
						new Offense("3rd", "7 days", PunishmentType.MUTE),
						new Offense("Other", "7 days", PunishmentType.MUTE)),

						Collections.singletonList(PunishmentType.MUTE)
				),
				new PunishReason("Misuse of /helpop or /report", Arrays.asList(
						new Offense("1st", "", PunishmentType.WARN),
						new Offense("2nd", "6 hours", PunishmentType.MUTE),
						new Offense("3rd", "12 hours", PunishmentType.BAN),
						new Offense("Other", "1 day", PunishmentType.BAN)),

						Arrays.asList(PunishmentType.MUTE, PunishmentType.WARN, PunishmentType.BAN)
				),
				new PunishReason("Block Glitching", Arrays.asList(
						new Offense("1st", "", PunishmentType.WARN),
						new Offense("1st", "1 day (If they Kill)", PunishmentType.BAN),
						new Offense("2nd", "3 days", PunishmentType.BAN),
						new Offense("3rd", "14 days", PunishmentType.BAN),
						new Offense("Other", "14 days", PunishmentType.BAN)),

						Arrays.asList(PunishmentType.WARN, PunishmentType.BAN)
				),
				new PunishReason("Pearl Glitching", Arrays.asList(
						new Offense("1st", "", PunishmentType.WARN),
						new Offense("1st", "1 day (If they Kill)", PunishmentType.BAN),
						new Offense("2nd", "3 days", PunishmentType.BAN),
						new Offense("3rd", "7 days", PunishmentType.BAN),
						new Offense("4th", "14 days", PunishmentType.BAN),
						new Offense("Other", "14 days", PunishmentType.BAN)),

						Arrays.asList(PunishmentType.WARN, PunishmentType.BAN)
				),
				new PunishReason("DTR Evasion", Arrays.asList(
						new Offense("1st", "1 day", PunishmentType.BAN),
						new Offense("2nd", "3 days", PunishmentType.BAN),
						new Offense("3rd", "7 days", PunishmentType.BAN),
						new Offense("Other", "7 days", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Griefing", Arrays.asList(
						new Offense("1st", "6 hours", PunishmentType.BAN),
						new Offense("2nd", "12 hours", PunishmentType.BAN),
						new Offense("3rd", "1 day", PunishmentType.BAN),
						new Offense("4th", "7 days", PunishmentType.BAN),
						new Offense("Other", "7 days", PunishmentType.BAN)),

						Arrays.asList(PunishmentType.BAN)
				),
				new PunishReason("PvP Timer Abuse (SOTW Timer As Well)", Arrays.asList(
						new Offense("1st", "3 hours", PunishmentType.BAN),
						new Offense("2nd", "6 hours", PunishmentType.BAN),
						new Offense("3rd", "12 hours", PunishmentType.BAN),
						new Offense("Other", "12 hours", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Allying", Arrays.asList(
						new Offense("1st", "1 day", PunishmentType.BAN),
						new Offense("2nd", "3 days", PunishmentType.BAN),
						new Offense("3rd", "7 days", PunishmentType.BAN),
						new Offense("4th", "14 days", PunishmentType.BAN),
						new Offense("Other", "7 days", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Kick And Killing", Arrays.asList(
						new Offense("1st", "3 days", PunishmentType.BAN),
						new Offense("2nd", "7 days", PunishmentType.BAN),
						new Offense("3rd", "30 days", PunishmentType.BAN),
						new Offense("Other", "30 days", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Mute Evasion", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.MUTE)),

						Collections.singletonList(PunishmentType.MUTE)
				),
				new PunishReason("Cheating (Admitted)", Collections.singletonList(
						new Offense("Only", "14 days", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Refusual To SS", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Cheats Found In SS", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Usage of VPN (On Orbit)", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Ban Evasion", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BLACKLIST)),

						Collections.singletonList(PunishmentType.BLACKLIST)
				),
				new PunishReason("DDoS Threats/Comedy", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BLACKLIST)),

						Collections.singletonList(PunishmentType.BLACKLIST)
				),
				new PunishReason("Blatant Cheating", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				),
				new PunishReason("Illegal Modifications", Collections.singletonList(
						new Offense("Only", "Permanent", PunishmentType.BAN)),

						Collections.singletonList(PunishmentType.BAN)
				)
		);
	}

}
