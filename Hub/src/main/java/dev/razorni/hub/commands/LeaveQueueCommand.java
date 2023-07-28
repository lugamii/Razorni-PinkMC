package dev.razorni.hub.commands;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.shits.Command;
import dev.razorni.hub.queue.QueueHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCommand extends Command {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(CC.RED + "No Console!");
            return false;
        }
        Player player = (Player)sender;
        if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null || Profile.getByUuid(player.getUniqueId()).getActiveBlacklist() != null) {
            sender.sendMessage(CC.RED + "You cannot join server while you have active punishment.");
            return true;
        }
        if (QueueHandler.getQueue(player) == null) {
            player.sendMessage(CC.translate("&cYou aren't in the queue!"));
        } else {
            player.sendMessage(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("QUEUE_MESSAGES.LEAVE_QUEUE"))
                    .replaceAll("%server%", QueueHandler.getQueueName(player)));
            QueueHandler.getQueue(player).removeEntry(player);
        }
        return false;
    }
}
