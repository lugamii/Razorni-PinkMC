/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 */
package dev.razorni.core.extras.xpacket;

import dev.razorni.core.Core;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class FrozenXPacketHandler {
    private static final String GLOBAL_MESSAGE_CHANNEL="XPacket:All";
    static final String PACKET_MESSAGE_DIVIDER="||";

    public static void init() {
        FrozenXPacketHandler.connectToServer(Core.getInstance().getLocalJedisPool());
    }

    public static void connectToServer(JedisPool connectTo) {

        Thread subscribeThread=new Thread(() -> {
            while (Core.getInstance().isEnabled()) {
                try {
                    Jedis jedis=connectTo.getResource();
                    Throwable throwable=null;
                    try {
                        XPacketPubSub pubSub=new XPacketPubSub();
                        String channel=GLOBAL_MESSAGE_CHANNEL;
                        jedis.subscribe(pubSub, channel);
                    } catch (Throwable throwable2) {
                        throwable=throwable2;
                        throw throwable2;
                    } finally {
                        if (jedis == null) continue;
                        if (throwable != null) {
                            try {
                                jedis.close();
                            } catch (Throwable throwable3) {
                                throwable.addSuppressed(throwable3);
                            }
                            continue;
                        }
                        jedis.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Core - xPacket Subscribe Thread");
        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    public static void sendToAll(XPacket packet) {
        FrozenXPacketHandler.send(packet, Core.getInstance().getBackboneJedisPool());
    }

    public static void sendToAllViaLocal(XPacket packet) {
        FrozenXPacketHandler.send(packet, Core.getInstance().getLocalJedisPool());
    }

    public static void send(XPacket packet, JedisPool sendOn) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            try (Jedis jedis=sendOn.getResource()) {
                String encodedPacket=packet.getClass().getName() + PACKET_MESSAGE_DIVIDER + Core.getInstance().getMongoHandler().getGSON().toJson(packet);
                jedis.publish(GLOBAL_MESSAGE_CHANNEL, encodedPacket);
            }
        });
    }

    private FrozenXPacketHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

