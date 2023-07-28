package dev.razorni.core.profile.command;

import dev.razorni.core.Core;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.util.CC;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/09/2021 / 8:50 AM
 * Core / rip.orbit.gravity.profile.command
 */
public class PlaytimeCommand {

	@Command(names = {"stafftime", "stime", "staffingtime"}, permission = "gravity.command.stafftime")
	public static void playTime(CommandSender sender, @Param(name = "player", defaultValue = "self")UUID uuid) {
		String server = Core.getInstance().getConfig().getString("SERVER_NAME");
		Profile profile = Profile.getByUuid(uuid);

		sender.sendMessage(CC.translate(profile.getDisplayName() + "&f's playtime is &6" + Punishment.TimeUtils.formatIntoDetailedString(profile.getPlayTimeMap().get(server))));

	}

	public static int ticksPlayed(Player player) {
		return player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK")) / 20;
	}

}
