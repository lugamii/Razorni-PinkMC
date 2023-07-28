package dev.razorni.core.commands.staff;
import dev.razorni.core.database.redis.packets.global.AlertPacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/07/2021 / 4:40 AM
 * Core / rip.orbit.gravity.commands.staff
 */

public class AlertCommand {

	@Command(names = "alert", permission = "gravity.command.alert")
	public static void alert(CommandSender sender, @Param(name = "msg", wildcard = true) String msg) {

//		Core.getInstance().getPacketBase().sendPacket(new PacketGlobalBroadcast(msg));

		new AlertPacket(msg).send();

//		new AlertPacket(new JsonBuilder().addProperty("message", msg)).send();
	}

}
