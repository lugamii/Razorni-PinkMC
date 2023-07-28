package dev.razorni.core.commands;
import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.ReportUpdatePacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.report.Report;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class RequestCommand {

    private static final long reportcooldown = TimeUnit.SECONDS.toMillis(60);

    @Command(names = {"request", "helpop"}, permission = "")
    public static void helpop(Player player, @Param(name = "message", wildcard = true) String request) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (!profile.getRequestCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "You must wait before you can request assistance again.");
            return;
        }

        player.sendMessage(CC.GREEN + "We have received your request and will attend to you soon. Please be patient.");

        profile.setRequestCooldown(new Cooldown(reportcooldown));
        profile.save();

        Report report = new Report("report-" + (Core.getInstance().getReportHandler().getReports().size() + 1), request, player.getName(), System.currentTimeMillis(), false);
        report.setServer(Core.getInstance().getConfig().getString("SERVER_NAME"));

        Core.getInstance().getReportHandler().saveReport(report);

        new ReportUpdatePacket(report, profile, true, false).send();

    }
}
