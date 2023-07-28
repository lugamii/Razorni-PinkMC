package dev.razorni.core.extras.xpacket;

import dev.razorni.core.Core;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

@NoArgsConstructor
public class XPacketPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        Class<?> packetClass;
        int packetMessageSplit=message.indexOf("||");
        String packetClassStr=message.substring(0, packetMessageSplit);
        String messageJson=message.substring(packetMessageSplit + "||".length());
        try {
            packetClass=Class.forName(packetClassStr);
        } catch (ClassNotFoundException ignored) {
            return;
        }
        XPacket packet=(XPacket) Core.getInstance().getMongoHandler().getGSON().fromJson(messageJson, packetClass);
        if (Core.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask(Core.getInstance(), packet::onReceive);
        }
    }
}

