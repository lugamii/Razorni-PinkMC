package dev.razorni.core.profile.punishment.command;

import dev.razorni.core.util.Locale;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.menu.PunishmentsMenu;


import org.bukkit.entity.Player;

import java.util.UUID;

public class HistoryCommand {

	@Command(names = {"history", "c", "punishments"}, permission = "gravity.command.history", async = true)
	public static void history(Player sender, @Param(name = "target") UUID uuid) {

		Profile profile = Profile.getByUuid(uuid);
		if (profile == null && !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		new PunishmentsMenu(profile).openMenu(sender);
	}

}
