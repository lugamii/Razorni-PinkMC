package dev.razorni.core.extras.xpacket;

import dev.razorni.core.Core;

public interface XPacket {

    default Core getGravity() {
        return Core.getInstance();
    }

    void onReceive();

    String getID();

    default void send() {
        FrozenXPacketHandler.sendToAll(this);
    }
}

