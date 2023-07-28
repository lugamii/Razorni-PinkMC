package dev.razorni.hub.queue;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.razorni.core.util.CC;
import dev.razorni.hub.Hub;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
@Getter
@Setter
public class QueueData {
    public static QueueData instance;
    private String server;
    private List<Player> players = new ArrayList();
    private boolean paused;
    private int limit;

    public QueueData(final String server) {
        this.server = server;
        this.paused = false;
        this.limit = 1000;
        (new BukkitRunnable() {
            public void run() {
                for(Player player : players) {
                    if(player.isOnline()) {
                        CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("QUEUE_MESSAGES.INQUEUE"))
                                .stream()
                                .map(s -> s.replace("%position%", String.valueOf(players.indexOf(player) + 1)))
                                .map(s -> s.replace("%server%", server))
                                .map(s -> s.replace("%inqueue%", String.valueOf(Hub.getInstance().getQueueManager().getInQueue(Hub.getInstance().getQueueManager().getQueueIn(player)))))
                                .forEach(player::sendMessage);
                    } else {
                        players.remove(player);
                    }
                }
            }
        }).runTaskTimer(Hub.getInstance(), 300L, 300L);
    }

    public int getPosition(Player player) {
        return players.indexOf(player) + 1;
    }

    public void addEntry(Player player) {
        if (player != null) {
            if (!this.players.contains(player)) {
                if (QueueHandler.getPriority(player) == 0) {
                    this.sendDirect(player);
                    player.sendMessage(CC.translate("&aYou have been sent to " + this.server + "."));
                } else {
                    this.players.add(player);
                    for(Player user : this.players) {
                        int pos = this.players.indexOf(user);
                        if(user != player && QueueHandler.getPriority(player) < QueueHandler.getPriority(user)) {
                            if(this.players.get(pos).isOnline()) {
                                this.players.get(pos).sendMessage(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("QUEUE_MESSAGES.HIGHER_QUEUE")));
                            }
                            Collections.swap(this.players, pos, this.players.size() - 1);
                        }
                    }
                }
            }
        }
    }

    public void removeEntry(Player player) {
        this.players.remove(player);
    }

    public Player getPlayerAt(int player) {
        return this.players.get(player);
    }

    public void sendFirst() {
        if (!this.players.isEmpty()) {
            Player p = this.players.get(0);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(this.server);
            p.sendPluginMessage(Hub.getInstance(), "BungeeCord", out.toByteArray());
        }

    }

    public void sendDirect(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(this.server);
        player.sendPluginMessage(Hub.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static QueueData getInstance() {
        return instance;
    }
}
