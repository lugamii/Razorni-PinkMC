package dev.razorni.core.chat.command;



import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 10:36 AM
 * Core / rip.orbit.gravity.chat.command
 */
public class SlowChatCommand {

	@Command(names = "slowchat", permission = "")
	public static void slowchat(CommandSender sender, @Param(name = "seconds") int seconds) {
		Core.getInstance().getChat().setDelayTime(seconds);
		Core.getInstance().getServer().broadcastMessage((CC.PINK + "Chat has just been slowed by " + sender.getName()));
	}

}
