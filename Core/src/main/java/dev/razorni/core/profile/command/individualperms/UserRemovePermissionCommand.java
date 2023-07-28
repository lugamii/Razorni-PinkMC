package dev.razorni.core.profile.command.individualperms;

import dev.razorni.core.Core;
import dev.razorni.core.util.Locale;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserRemovePermissionCommand {

    @Command(names = "user removeperm", permission = "gravity.command.user", async = true, description = "Removes a permission to a players profile.")
    public static void removePerm(CommandSender sender, @Param(name = "player") UUID uuid, @Param(name = "permission") String permission) {
        Profile profile = Profile.getByUuid(uuid);
        if (profile == null || !profile.isLoaded()) {
            sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        if (!profile.getIndividualPermissions().contains(permission)) {
            sender.sendMessage(CC.RED + profile.getUsername() + " doesn't have the permission " + permission + '!');
            return;
        }

        profile.getIndividualPermissions().remove(permission);
        Player player = profile.getPlayer();
        if (player != null) {
            Core.getInstance().getServer().getScheduler().runTask(Core.getInstance(), () -> {
                profile.setupPermissionsAttachment(Core.getInstance(), player);
                sender.sendMessage(CC.YELLOW + "Recalculated permissions for " + player.getName() + '.');
            });
        }
        profile.save();
        sender.sendMessage(CC.GREEN + "Removed permission " + "\'" + permission + "\' for " + profile.getUsername() + '.');
    }
}
