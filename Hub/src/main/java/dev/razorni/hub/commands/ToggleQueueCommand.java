package dev.razorni.hub.commands;

import dev.razorni.core.util.CC;
import dev.razorni.hub.Hub;
import dev.razorni.hub.queue.QueueData;
import dev.razorni.hub.queue.QueueHandler;
import dev.razorni.hub.utils.shits.Command;
import org.bukkit.command.CommandSender;


public class ToggleQueueCommand extends Command {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.translate("&cUsage: /togglequeue <queue>"));
        } else {
            String server = args[0];
            if (QueueHandler.getQueue(server) == null) {
                sender.sendMessage(CC.translate("&cQueue named " + server + " doesn't exists!"));
                return false;
            }

            QueueData queue = QueueHandler.getQueue(server);
            if (queue.isPaused()) {
                sender.sendMessage(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("QUEUE_MESSAGES.UNPAUSED"))
                        .replaceAll("%server%", queue.getServer()));
            } else {
                sender.sendMessage(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("QUEUE_MESSAGES.PAUSED"))
                        .replaceAll("%server%", queue.getServer()));
            }

            queue.setPaused(!queue.isPaused());
        }
        return false;
    }
}
