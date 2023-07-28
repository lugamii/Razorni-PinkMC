package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import org.bukkit.potion.PotionEffect;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand {

	@Command(names = { "clearinv", "clear", "ci" }, permission = "gravity.command.clearinv")
	public static void clearchat(CommandSender sender, @Param(name = "player", defaultValue = "self") Player player) {
		player.getInventory().setContents(new ItemStack[36]);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.updateInventory();
		player.sendMessage(CC.translate("&fYour inventory has been &6cleared&f by " + sender.getName()));

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

}
