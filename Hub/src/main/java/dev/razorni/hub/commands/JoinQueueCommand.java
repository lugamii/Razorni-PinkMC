package dev.razorni.hub.commands;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.hub.Hub;
import dev.razorni.hub.queue.QueueData;
import dev.razorni.hub.queue.QueueHandler;
import dev.razorni.hub.utils.shits.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinQueueCommand extends Command {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(CC.RED + "No Console!");
            return false;
        }
        Player player = (Player) sender;
        if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null || Profile.getByUuid(player.getUniqueId()).getActiveBlacklist() != null) {
            sender.sendMessage(CC.RED + "You cannot join server while you have active punishment.");
            return true;
        }
        if(args.length != 1) {
            sender.sendMessage(CC.translate("&c/play <queuename>"));
        } else {
            QueueData queue = QueueHandler.getQueue(args[0]);
            if(queue == null) {
                player.sendMessage(CC.translate("&cThe queue " + args[0] + " not exists"));
            } else {
                if(queue.isPaused()) {
                    player.sendMessage(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("QUEUE_MESSAGES.PAUSED")
                            .replace("%server%", queue.getServer())));
                } else {
                    queue.addEntry(player);
                }
            }
        }
        return true;
    }
}
