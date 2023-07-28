package dev.razorni.core.commands;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Sound;
import dev.razorni.core.profile.Profile;
import org.bukkit.entity.Player;

public class MessageCommand {

    @Command(names = {"message", "msg", "m", "tell", "whisper"}, permission = "")
    public static void message(Player player, @Param(name = "target") Profile target, @Param(name = "message", wildcard = true) String message) {
        if (target == null) {
            player.sendMessage(CC.RED + "This player is not online.");
            return;
        }

        Player targetPlayer = target.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }

        if (!player.hasPermission("gravity.staff")) {
            if (target.getIgnored().contains(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cThat player has you ignored."));
                return;
            }

            if (!target.getOptions().isPrivateChatEnabled()) {
                player.sendMessage(CC.RED + "This player has messages disabled.");
                return;
            }

            if (!profile.getOptions().isPrivateChatEnabled()) {
                player.sendMessage(CC.RED + "You have messages disabled.");
                return;
            }
        }

        String senderName = CC.RESET + player.getDisplayName();
        String targetName = CC.RESET + target.getDisplayName();

        profile.setReplyTo(targetPlayer.getUniqueId());
        target.setReplyTo(player.getUniqueId());

        String toMessage = CC.GRAY + "(To " + targetName + CC.GRAY + ") " + message;
        String fromMessage = CC.GRAY + "(From " + senderName + CC.GRAY + ") " + message;

        if (target.getOptions().isSoundsEnabled()) {
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.ORB_PICKUP, 2F, 2F);
        }

        player.sendMessage(toMessage);
        targetPlayer.sendMessage(fromMessage);
    }
}
