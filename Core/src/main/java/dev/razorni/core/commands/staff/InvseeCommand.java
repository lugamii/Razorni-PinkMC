package dev.razorni.core.commands.staff;


import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;

public class InvseeCommand {

	@Command(names = "invsee", permission = "gravity.command.invsee")
	public static void invsee(Player player, @Param(name = "target") Player target) {
		player.openInventory(target.getInventory());
	}
}
