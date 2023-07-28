package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;

import org.bukkit.entity.Player;

public class MoreCommand {

	@Command(names = "more", permission = "gravity.command.more")
	public static void more(Player player) {
		if (player.getItemInHand() == null) {
			player.sendMessage(CC.RED + "You must be holding an item.");
			return;
		}

		player.getItemInHand().setAmount(64);
		player.updateInventory();
		player.sendMessage(CC.translate("&fYour inventory has been &6cleared&f."));
	}

}
