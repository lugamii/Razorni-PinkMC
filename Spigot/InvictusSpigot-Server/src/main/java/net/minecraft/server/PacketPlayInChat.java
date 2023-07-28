package net.minecraft.server;

import eu.vortexdev.invictusspigot.InvictusSpigot;

import java.io.IOException;

public class PacketPlayInChat implements Packet<PacketListenerPlayIn> {

    private String a;

    public PacketPlayInChat() {}

    public PacketPlayInChat(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }

        this.a = s;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(100);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(final PacketListenerPlayIn packetlistenerplayin) {
        if ( !a.startsWith("/") )
        {
            InvictusSpigot.INSTANCE.getThreadingManager().getChatThreadPool().submit(() -> packetlistenerplayin.a(PacketPlayInChat.this));
            return;
        }
        // Spigot End
        packetlistenerplayin.a(this);
    }

    public String a() {
        return this.a;
    }
}
