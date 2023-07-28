package dev.razorni.core.database.redis.packets.global;

import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import dev.razorni.core.extras.reminder.Reminder;
import dev.razorni.core.extras.xpacket.XPacket;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 5:27 PM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class ReminderUpdatePacket implements XPacket {

	private Reminder reminder;
	private boolean remove;
	private boolean announce;

	@Override
	public void onReceive() {
		if (remove) {
			Core.getInstance().getReminderHandler().getReminders().remove(reminder);
		} else {
			Core.getInstance().getReminderHandler().saveReminder(reminder, false);
		}

		FancyMessage fancyMessage = new FancyMessage();
		fancyMessage.text(CC.translate("&fYou have just receive a new notification "));
		fancyMessage.then().text(CC.translate("&7&o(( Hover ))")).tooltip(CC.translate("&7Click to view all your unread notifications")).command("/reminders");

		if (announce) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				fancyMessage.send(player);
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2F, 2F);
			});
		}

		System.out.println("Established the " + getID() + " Packet");
	}

	@Override
	public String getID() {
		return "Reminder Update";
	}
}
