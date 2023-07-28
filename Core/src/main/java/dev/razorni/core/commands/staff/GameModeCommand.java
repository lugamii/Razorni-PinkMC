package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand {
//
//	@Command(names = {"gm", "gamemode"}, permission = "gravity.command.gamemode")
//	public static void gamemode(Player sender, @Param(name = "gamemode") String gamemode) {
//		if (gamemode.equalsIgnoreCase("c") || gamemode.equalsIgnoreCase("creative")) {
//			sender.setGameMode(GameMode.CREATIVE);
//			sender.sendMessage(CC.GOLD + "Your gamemode has been updated.");
//		} else if (gamemode.equalsIgnoreCase("s") || gamemode.equalsIgnoreCase("survival")) {
//			sender.setGameMode(GameMode.SURVIVAL);
//			sender.sendMessage(CC.GOLD + "Your gamemode has been updated.");
//		} else {
//			sender.sendMessage(CC.translate("&cInvalid gamemode"));
//		}
//	}

	@Command(names = {"gm", "gamemode"}, permission = "gravity.command.gamemode")
	public static void gamemode(Player sender, @Param(name = "gamemode") String gamemode, @Param(name = "target", defaultValue = "self") Player target) {
		if (gamemode.equalsIgnoreCase("c") || gamemode.equalsIgnoreCase("creative")) {
			target.setGameMode(GameMode.CREATIVE);
			if (target != sender) {
				target.sendMessage(CC.GOLD + "Your gamemode has been updated.");
			}
			sender.sendMessage(CC.GOLD + "Their gamemode has been updated.");
		} else if (gamemode.equalsIgnoreCase("s") || gamemode.equalsIgnoreCase("survival")) {
			target.setGameMode(GameMode.SURVIVAL);
			if (target != sender) {
				target.sendMessage(CC.GOLD + "Your gamemode has been updated.");
			}
			sender.sendMessage(CC.GOLD + "Their gamemode has been updated.");
		} else {
			sender.sendMessage(CC.translate("&cInvalid gamemode"));
		}
	}

}
