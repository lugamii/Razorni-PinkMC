package dev.razorni.hub.queue;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class QueueManager {

    public boolean inQueue(Player player) {
        return QueueHandler.getQueue(player) != null;
    }

    public void sendPlayer(Player player, String server) {
        Bukkit.getServer().dispatchCommand(player, "play " + server);
    }

    public String getQueueIn(Player player) {
        return QueueHandler.getQueue(player).getServer();
    }

    public int getPosition(Player player) {
        return QueueHandler.getQueue(player).getPosition(player);
    }

    public int getInQueue(String queue) {
        return QueueHandler.getQueue(queue).getPlayers().size();
    }
}
