package dev.razorni.core.commands;
import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.ReportUpdatePacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.report.Report;

import java.util.concurrent.TimeUnit;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 30/06/2021 / 11:27 AM
 * Core / rip.orbit.gravity.essentials.command
 */

public class ReportCommand {

	private static final long reportcooldown = TimeUnit.SECONDS.toMillis(60);

	@Command(names = {"report", "imbeingcheatedonfuckinghelpme"}, permission = "")
	public static void report(Player player, @Param(name = "target") Player target, @Param(name = "message", wildcard = true) String reason) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (!profile.getReportCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "You must wait before you can request assistance again.");
			return;
		}

		player.sendMessage(CC.GREEN + "We have received your report and will attend to you soon. Please be patient.");

		profile.setReportCooldown(new Cooldown(reportcooldown));
		profile.save();

		Report report = new Report("report-" + (Core.getInstance().getReportHandler().getReports().size() + 1), reason, player.getName(), System.currentTimeMillis(), true);
		report.setReported(target.getName());
		report.setServer(Core.getInstance().getConfig().getString("SERVER_NAME"));

		Core.getInstance().getReportHandler().saveReport(report);

		new ReportUpdatePacket(report, profile, true, false).send();
	}

}
