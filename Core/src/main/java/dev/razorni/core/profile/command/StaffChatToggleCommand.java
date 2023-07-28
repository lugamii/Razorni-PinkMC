package dev.razorni.core.profile.command;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.profile.Profile;

import org.bukkit.entity.Player;

public class StaffChatToggleCommand {

	@Command(names = {"recording", "togglestaffchatmessages" }, permission = "gravity.staff")

	public static void recording(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		profile.getStaffOptions().setStaffChatMessagesToggled(!profile.getStaffOptions().isStaffChatMessagesToggled());

		player.sendMessage(profile.getStaffOptions().isStaffChatMessagesToggled() ? CC.GREEN + "You have enabled your staff chat." : CC.RED + "You have disabled your staff chat.");
	}
}
