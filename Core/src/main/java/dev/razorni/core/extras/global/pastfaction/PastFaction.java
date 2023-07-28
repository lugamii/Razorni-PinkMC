package dev.razorni.core.extras.global.pastfaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 11:30 PM
 * Core / rip.orbit.gravity.profile.global
 */

@Data
@AllArgsConstructor
public class PastFaction {

	@Getter private static PastFactionJsonDeserializer deserializer = new PastFactionJsonDeserializer();
	@Getter private static PastFactionJsonSerializer serializer = new PastFactionJsonSerializer();

	private String faction;

}
