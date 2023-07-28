package dev.razorni.core.profile.command.individualperms;

import dev.razorni.core.util.Locale;
import dev.razorni.core.profile.permission.TimedPermission;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;


import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UserInfoCommand {


    @Command(names = "user info", permission = "gravity.command.user", async = true, description = "Show information on a players profile.")
    public static void info(CommandSender sender, @Param(name = "player")UUID uuid) {
        Profile profile = Profile.getByUuid(uuid);
        if (profile == null || !profile.isLoaded()) {
            sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.WHITE + "Showing Information for " + CC.GOLD + profile.getUsername() + CC.WHITE + '.');
        sender.sendMessage(CC.WHITE + "Active Rank: " + profile.getActiveRank().formattedName());
        sender.sendMessage(CC.WHITE + "Permissions");
        for (String permission : profile.getIndividualPermissions()) {
            sender.sendMessage(CC.GOLD + " - " + permission);
        }

        sender.sendMessage(CC.WHITE + "Timed Permissions");
        for (TimedPermission permission : profile.getTimedPermissions()) {
            sender.sendMessage(CC.GOLD + " - " + permission.getPermission() + CC.WHITE + "(" + permission.getTimeRemaining() + ")");
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

}
