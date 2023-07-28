package dev.razorni.core.profile.command.individualperms;

import dev.razorni.core.Core;
import dev.razorni.core.util.Locale;
import dev.razorni.core.profile.permission.TimedPermission;
import dev.razorni.core.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;

import java.util.UUID;

public class UserRemoveTimedPermissionCommand {

    @Command(names = "user removetimedperm", permission = "gravity.command.user", async = true, description = "Removes a temporary permission to a players profile.")
    public static void removePerm(CommandSender sender, @Param(name = "player") UUID uuid, @Param(name = "permission") String permission) {
        Profile profile = Profile.getByUuid(uuid);

        if (profile == null || !profile.isLoaded()) {
            sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        TimedPermission timedPermission = profile.timedPermissionByPerm(permission);

        if (timedPermission != null) {
            if (!profile.getTimedPermissions().contains(timedPermission)) {
                sender.sendMessage(CC.RED + profile.getUsername() + " does not have the permission " + permission + '!');
                return;
            }
        }

        profile.getTimedPermissions().remove(profile.timedPermissionByPerm(permission));
        Player player = profile.getPlayer();
        if (player != null) {
            Core.getInstance().getServer().getScheduler().runTask(Core.getInstance(), () -> {
                profile.setupPermissionsAttachment(Core.getInstance(), player);
                sender.sendMessage(CC.YELLOW + "Recalculated permissions for " + player.getName() + '.');
            });
        }
        profile.save();
        sender.sendMessage(CC.GREEN + "Removed temporary permission " + "\'" + permission + "\' for " + profile.getUsername() + '.');
    }

}