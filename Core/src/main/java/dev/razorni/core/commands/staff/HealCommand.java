package dev.razorni.core.commands.staff;

import dev.razorni.core.util.Locale;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand {

	@Command(names = "heal", permission = "gravity.command.heal")
	public static void heal(CommandSender sender, @Param(name = "target", defaultValue = "self") Player player) {
		if (player == null) {
			sender.sendMessage(Locale.PLAYER_NOT_FOUND.format());
			return;
		}

		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(5.0F);
		player.updateInventory();

		player.sendMessage(CC.translate("&fYou have been &6healed&f by " + sender.getName()));
	}

}
