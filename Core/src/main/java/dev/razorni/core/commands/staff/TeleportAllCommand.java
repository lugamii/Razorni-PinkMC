package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportAllCommand {
	@Command(names = "tpall", async = true, permission = "gravity.command.teleport.all")
	public static void tpall(Player player) {
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other == player)
				continue;
			other.teleport(player);
		}
		player.sendMessage(CC.YELLOW + "Everyone on the server has been teleported to you.");
	}
}
