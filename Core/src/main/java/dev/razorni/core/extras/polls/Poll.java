package dev.razorni.core.extras.polls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:57 PM
 * HCTeams / rip.orbit.hcteams.polls
 */

@AllArgsConstructor
@Data
public class Poll {

	@Getter public static List<Poll> polls = new ArrayList<>();

	private String title;
	private String question;
	private int yes = 0;
	private int no = 0;

	public List<UUID> claimedPlayers;

}
