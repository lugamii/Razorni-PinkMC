package dev.razorni.hub.queue.listener;

import dev.razorni.hub.queue.QueueData;
import dev.razorni.hub.queue.QueueHandler;
import dev.razorni.hub.utils.PluginListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@PluginListener
public class QueueListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for(QueueData queue : QueueHandler.queues) {
            if (queue.getPlayers().contains(player)) {
                queue.removeEntry(player);
            }
        }

    }
}
