package net.minecraft.server;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutMapChunkBulk implements Packet<PacketListenerPlayOut> {

    private int[] a;
    private int[] b;
    private PacketPlayOutMapChunk.ChunkMap[] c;
    private boolean d;
    private World world; // Spigot

    public PacketPlayOutMapChunkBulk() {
    }

    public PacketPlayOutMapChunkBulk(List<Chunk> list) {
        int i = list.size();

        this.a = new int[i];
        this.b = new int[i];
        this.c = new PacketPlayOutMapChunk.ChunkMap[i];

        if (i == 0) {
            return;
        }

        Chunk first = list.get(0);

        this.world = first.getWorld();
        this.d = !world.worldProvider.o();
        this.a[0] = first.locX;
        this.b[0] = first.locZ;
        this.c[0] = first.getChunkMap(true, '\uffff');

        for (int j = 1; j < i; ++j) {
            Chunk chunk = list.get(j);
            this.a[j] = chunk.locX;
            this.b[j] = chunk.locZ;
            this.c[j] = chunk.getChunkMap(true, '\uffff');
        }
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeBoolean(this.d);
        packetdataserializer.b(this.c.length);

        int i;

        for (i = 0; i < this.a.length; ++i) {
            packetdataserializer.writeInt(this.a[i]);
            packetdataserializer.writeInt(this.b[i]);
            packetdataserializer.writeShort((short) (this.c[i].b & '\uffff'));
        }

        for (i = 0; i < this.a.length; ++i) {
            world.spigotConfig.antiXrayInstance.obfuscate(this.a[i], this.b[i], this.c[i].b, this.c[i].a, world); // Spigot
            packetdataserializer.writeBytes(this.c[i].a);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
