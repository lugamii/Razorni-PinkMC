package dev.razorni.core.profile.option.commands;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.profile.Profile;
import org.bukkit.entity.Player;

public class TogglePrivateMessagesCommand {

	@Command(names = {"togglepm", "togglepms", "tpm", "tpms"}, permission = "")

	public static void togglepms(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		profile.getOptions().setPrivateChatEnabled(!profile.getOptions().isPrivateChatEnabled());
		profile.save();

		player.sendMessage(profile.getOptions().isPrivateChatEnabled() ? CC.GREEN + "You have enabled private messages." : CC.RED + "You have disabled private messages.");
	}
}
