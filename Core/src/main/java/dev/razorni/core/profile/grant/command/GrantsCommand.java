package dev.razorni.core.profile.grant.command;

import dev.razorni.core.util.Locale;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.menu.GrantsMenu;


import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand {

	@Command(names = "grants", permission = "gravity.command.grants")
	public static void grants(Player sender, @Param(name = "player") UUID target) {
		Profile profile = Profile.getByUuid(target);

		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		new GrantsMenu(profile).openMenu(sender);
	}

}
