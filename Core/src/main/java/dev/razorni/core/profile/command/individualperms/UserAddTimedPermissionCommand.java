package dev.razorni.core.profile.command.individualperms;

import dev.razorni.core.Core;
import dev.razorni.core.util.Locale;
import dev.razorni.core.profile.permission.TimedPermission;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.duration.Duration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import java.util.UUID;

public class UserAddTimedPermissionCommand {

    @Command(names = "user addtimedperm", permission = "gravity.command.user", async = true, description = "Adds a temporary permission to a players profile.")
    public static void removePerm(CommandSender sender, @Param(name = "player") UUID uuid, @Param(name = "permission") String permission, @Param(name = "time") String time) {
        Profile profile = Profile.getByUuid(uuid);
        Duration duration = Duration.fromString(time);
        if (profile == null || !profile.isLoaded()) {
            sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        TimedPermission timedPermission = profile.timedPermissionByPerm(permission);

        if (timedPermission != null) {
            if (profile.getTimedPermissions().contains(timedPermission)) {
                sender.sendMessage(CC.RED + profile.getUsername() + " already has the permission " + permission + '!');
                return;
            }
        }
        profile.getTimedPermissions().add(new TimedPermission(permission, System.currentTimeMillis(), duration.getValue()));
        Player player = profile.getPlayer();
        if (player != null) {
            Core.getInstance().getServer().getScheduler().runTask(Core.getInstance(), () -> {
                profile.setupPermissionsAttachment(Core.getInstance(), player);
                sender.sendMessage(CC.YELLOW + "Recalculated permissions for " + player.getName() + '.');
            });
        }
        profile.save();
        sender.sendMessage(CC.GREEN + "Added temporary permission " + "\'" + permission + "\' for " + profile.getUsername() + '.' + " Time: " + time);
    }

}