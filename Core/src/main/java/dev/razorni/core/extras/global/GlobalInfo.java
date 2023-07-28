package dev.razorni.core.extras.global;

import dev.razorni.core.extras.global.pastfaction.PastFaction;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 11:05 PM
 * Core / rip.orbit.gravity.profile.global
 */

@Getter @Setter
public class GlobalInfo {

	private List<PastFaction> pastFactions = new ArrayList<>();

	private int hcfKills = 0;
	private int hcfDeaths = 0;
	private int hcfMadeRaidable = 0;
	private int hcfCurrentKillstreak = 0;
	private int hcfHighestKillstreak = 0;
	private int hcfMapsPlayed = 0;
	private int hcfKothCaps = 0;
	private int hcfCitadelCaps = 0;
	private int hcfConquestCaps = 0;

	private int kitsKills = 0;
	private int kitsDeaths = 0;
	private int kitsTournyWins = 0;
	private int kitsTournyLoses = 0;
	private int kitsTournyPlayed = 0;
	private int kitsCurrentKillstreak = 0;
	private int kitsHighestKillstreak = 0;
	private int kitsSeasonsPlayed = 0;
	private int kitsKothCaps = 0;
	private int kitsCitadelCaps = 0;
	private int kitsConquestCaps = 0;

	private int practiceKills = 0;
	private int practiceDeaths = 0;
	private int practiceWins = 0;
	private int practiceLoses = 0;
	private int practiceGamesPlayed = 0;
	private int practiceCurrentWinstreak = 0;
	private int practiceHighestWinstreak = 0;
	private int practiceSeasonsPlayed = 0;

}
