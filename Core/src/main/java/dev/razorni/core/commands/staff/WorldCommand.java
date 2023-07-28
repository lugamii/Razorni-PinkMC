package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand {
	@Command(names = "world", permission = "gravity.command.world")
	public static void world(Player player, @Param(name = "world") String newworld) {
		World world = Bukkit.getWorld(newworld);

		if (world == null) {
			player.sendMessage(CC.RED + "World not found!");
			return;
		}

		if (player.getWorld().equals(world)) {
			player.sendMessage(CC.RED + "You are already in this world!");
		}

		Location playerlocation = player.getLocation();
		Location newlocation = new Location(world, playerlocation.getX(), playerlocation.getY(), playerlocation.getZ(), playerlocation.getYaw(), playerlocation.getPitch());
		player.teleport(newlocation);
		player.sendMessage(CC.translate("&fYou have been &6teleported&f to the &6" + newworld + "&f World"));
	}
}
