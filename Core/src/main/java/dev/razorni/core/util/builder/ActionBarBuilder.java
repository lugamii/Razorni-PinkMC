package dev.razorni.core.util.builder;

import dev.razorni.core.util.CC;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ActionBarBuilder {

    private static final Map<Player, BukkitTask> PENDING_MESSAGES = new HashMap<>();

    public static void sendActionBarMessage(Player player, String message) {
        sendRawActionBarMessage(player, "{\"text\": \"" + CC.translate(message) + "\"}");
    }

    public static void sendRawActionBarMessage(Player player, String rawMessage) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(rawMessage);
        sendActionBar(player, chatBaseComponent);
    }

    public static void sendActionBar(Player player, IChatBaseComponent component) {
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(component, (byte) 2);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    public static void sendActionBarMessage(final Player player, final String message, final int duration, Plugin plugin) {
        cancelPendingMessages(player);
        final BukkitTask messageTask = new BukkitRunnable() {
            private int count = 0;

            @Override
            public void run() {
                if (count >= (duration - 3)) {
                    this.cancel();
                }
                sendActionBarMessage(player, message);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        PENDING_MESSAGES.put(player, messageTask);
    }

    private static void cancelPendingMessages(Player player) {
        if (PENDING_MESSAGES.containsKey(player)) {
            PENDING_MESSAGES.get(player).cancel();
        }
    }
}