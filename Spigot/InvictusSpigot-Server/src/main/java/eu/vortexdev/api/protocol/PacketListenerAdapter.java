package eu.vortexdev.api.protocol;

import net.minecraft.server.Packet;
import net.minecraft.server.PlayerConnection;

public interface PacketListenerAdapter {
    default void onSend(PlayerConnection playerConnection, Packet packet) {
    }

    default void onReceive(PlayerConnection playerConnection, Packet packet) {
    }
}
