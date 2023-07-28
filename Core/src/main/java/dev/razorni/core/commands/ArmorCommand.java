package dev.razorni.core.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.util.command.Command;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/08/2021 / 4:29 PM
 * Core / rip.orbit.gravity.essentials
 */
public class ArmorCommand {

	@Command(names = {"almostbreak", "breakalmost", "breakdown"}, permission = "gravity.command.break")
	public static void almostBreak(Player sender) {

		ItemStack stack = sender.getItemInHand();

		stack.setDurability(stack.getType().getMaxDurability());

	}

	@Command(names = {"repair", "fix", "fixhand"}, permission = "gravity.command.repair")
	public static void fix(Player sender) {

		ItemStack stack = sender.getItemInHand();

		stack.setDurability((short) 0);

	}

}
