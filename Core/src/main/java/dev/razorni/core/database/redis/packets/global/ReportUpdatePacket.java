package dev.razorni.core.database.redis.packets.global;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.razorni.core.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.report.Report;
import dev.razorni.core.extras.xpacket.XPacket;

import java.util.concurrent.TimeUnit;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 28/08/2021 / 4:10 AM
 * Core / rip.orbit.gravity.database.redis.packets
 */

@AllArgsConstructor
@Data
public class ReportUpdatePacket implements XPacket {

	private Report report;
	private Profile sender;
	private boolean sendMessage;
	private boolean remove;

	public static final long requestcooldown = TimeUnit.SECONDS.toMillis(60);

	@Override
	public void onReceive() {

		if (sendMessage) {
			FancyMessage message = new FancyMessage();
			if (report.isReport()) {

				message.text(CC.translate("&7 » &fMessage Sender "));
				message.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to message " + report.getSender())).suggest("/msg " + report.getSender() + " ");

				FancyMessage messageReported = new FancyMessage();
				messageReported.text(CC.translate("&7 » &fMessage Reported "));
				messageReported.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to message " + report.getSender())).suggest("/msg " + report.getReported() + " ");

				FancyMessage teleportSender = new FancyMessage();
				teleportSender.text(CC.translate("&7 » &fTeleport To Sender "));
				teleportSender.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to teleport to " + report.getSender())).command("/tp " + report.getSender());

				FancyMessage teleportTarget = new FancyMessage();
				teleportTarget.text(CC.translate("&7 » &fTeleport To Target "));
				teleportTarget.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to teleport to " + report.getReported())).command("/tp " + report.getReported());

				Bukkit.getOnlinePlayers()
						.stream()
						.filter(player -> player.hasPermission("gravity.command.report.see"))
						.forEach(player -> {
							player.sendMessage(CC.translate("&9[Report] &b(" + report.getServer() + "&b) &7" + report.getSender() + " &bhas &breported &7" + report.getReported()));
							player.sendMessage(CC.translate("   &9Reason: &7") + report.getMessage());
						});
				sender.setRequestCooldown(new Cooldown(requestcooldown));
			} else {

				message.text(CC.translate("&7 » &fMessage Player "));
				message.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to message " + report.getSender())).suggest("/msg " + report.getSender() + " ");

				FancyMessage teleport = new FancyMessage();
				teleport.text(CC.translate("&7 » &fTeleport To Player "));
				teleport.then().text(CC.translate("&6(( CLICK HERE ))")).tooltip(CC.translate("&eClick here to teleport to " + report.getSender())).command("/tp " + report.getSender());

				Bukkit.getOnlinePlayers()
						.stream()
						.filter(player -> player.hasPermission("gravity.command.helpop.see"))
						.forEach(player -> {
							player.sendMessage(CC.translate("&9[Request] &b(" + report.getServer() + "&b) &7" + report.getSender() + " &bhas &brequested assistance&7"));
							player.sendMessage(CC.translate("   &9Reason: &7") + report.getMessage());
						});

				sender.setReportCooldown(new Cooldown(requestcooldown));
			}
		}

		if (!remove) {
			getGravity().getReportHandler().getReports().remove(getGravity().getReportHandler().getReportByName(report.getName()));
			getGravity().getReportHandler().getReports().add(report);
		} else {
			getGravity().getReportHandler().getReports().remove(report);
		}

		System.out.println("Established the " + getID() + " Packet");

	}

	@Override
	public String getID() {
		return "Report Update";
	}
}
