package dev.razorni.core.commands;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Sound;
import dev.razorni.core.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ReplyCommand {

    @Command(names = {"reply", "r"}, permission = "")
    public static void reply(Player player, @Param(name = "message", wildcard = true) String message) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getReplyTo() == null) {
            player.sendMessage(CC.RED + "You have nobody to reply to.");
            return;
        }

        final Player target = Bukkit.getPlayer(profile.getReplyTo());

        if (target == null || !target.isOnline()) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }

        Profile targetData = Profile.getByUuid(target.getUniqueId());

        if (!player.hasPermission("gravity.staff")) {
            if (!targetData.getOptions().isPrivateChatEnabled()) {
                player.sendMessage(CC.RED + "That player has their messages off.");
                return;
            }

            if (targetData.getIgnored().contains(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cThat player has you ignored."));
                return;
            }
        }

        String senderName = CC.RESET + player.getDisplayName();
        String targetName = CC.RESET + target.getDisplayName();

        profile.setReplyTo(target.getUniqueId());
        targetData.setReplyTo(player.getUniqueId());

        String toMessage = CC.GRAY + "(To " + targetName + CC.GRAY + ") " + message;
        String fromMessage = CC.GRAY + "(From " + senderName + CC.GRAY + ") " + message;

        if (targetData.getOptions().isSoundsEnabled()) {
            target.playSound(target.getLocation(), Sound.ORB_PICKUP, 2F, 2F);
        }

        player.sendMessage(toMessage);
        target.sendMessage(fromMessage);
    }
}
