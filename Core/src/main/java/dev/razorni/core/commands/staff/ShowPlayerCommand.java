package dev.razorni.core.commands.staff;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;

public class ShowPlayerCommand {

	@Command(names = "showplayer", permission = "gravity.command.showplayer")
	public static void showplayer(Player player, @Param(name = "target") Player target) {
		player.showPlayer(target);
		player.sendMessage(CC.translate("&aPlayer has been successfully shown."));
	}
}
