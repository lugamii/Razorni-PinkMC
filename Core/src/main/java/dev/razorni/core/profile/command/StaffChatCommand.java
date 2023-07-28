package dev.razorni.core.profile.command;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.builder.TitleBuilder;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.profile.Profile;

import org.bukkit.entity.Player;

public class StaffChatCommand {

	@Command(names = { "staffchat", "sc" }, permission = "gravity.command.staffchat")
	public static void staffchat(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());

		if (!profile.getStaffOptions().isStaffChatMessagesToggled()) {
			player.sendMessage(CC.RED + "If you aren't seeing messages it's because your staff chat messages are toggled... (/togglestaffchatmessages)");
		}

		if (profile.getStaffOptions().isStaffChat()) {
			profile.getStaffOptions().setStaffChat(false);
			player.sendMessage(CC.translate("&fYou have just &cdisabled&f your &6Staff Chat&f."));
			TitleBuilder title = new TitleBuilder("&d&lSTAFF CHAT", "&fDisabled", 20, 60, 20);
			title.send(player);
		} else {
			profile.getStaffOptions().setStaffChat(true);
			player.sendMessage(CC.translate("&fYou have just &aenabled&f your &6Staff Chat&f."));
			TitleBuilder title = new TitleBuilder("&d&lSTAFF CHAT", "&fEnabled", 20, 60, 20);
			title.send(player);
		}

	}

}
