package net.minecraft.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private ChunkMap c;
    private boolean d;

    public PacketPlayOutMapChunk() {
    }

    public PacketPlayOutMapChunk(Chunk chunk, boolean flag, int i) {
        this.a = chunk.locX;
        this.b = chunk.locZ;
        this.d = flag;
        this.c = chunk.getChunkMap(flag, i);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeInt(this.b);
        packetdataserializer.writeBoolean(this.d);
        packetdataserializer.writeShort((short) (c.b & '\uffff'));
        if (this.d && this.c.b == 0) { // Vortex - Do not serialize blocks for chunk unloading
        	packetdataserializer.b(0);
            return;
        }
        packetdataserializer.a(c.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    protected static int a(int i, boolean flag, boolean flag1) {
        return (i * 2 * 16 * 16 * 16) + (i * 16 * 16 * 16 / 2) + (flag ? i * 16 * 16 * 16 / 2 : 0) + (flag1 ? 256 : 0);
    }

    public static ChunkMap a(Chunk chunk, boolean flag, boolean flag1, int i) {
        ChunkSection[] achunksection = chunk.getSections();
        ChunkMap packetplayoutmapchunk_chunkmap = new ChunkMap();
        ArrayList<ChunkSection> arraylist = new ArrayList<>();

        int j;

        for (j = 0; j < achunksection.length; ++j) {
            ChunkSection chunksection = achunksection[j];

            if (chunksection != null && (!flag || !chunksection.a()) && (i & 1 << j) != 0) {
                packetplayoutmapchunk_chunkmap.b |= 1 << j;
                arraylist.add(chunksection);
            }
        }

        packetplayoutmapchunk_chunkmap.a = new byte[a(Integer.bitCount(packetplayoutmapchunk_chunkmap.b), flag1, flag)];
        j = 0;

        for(ChunkSection chunksection1 : arraylist) {
            for (char c0 : chunksection1.getIdArray()) {
                packetplayoutmapchunk_chunkmap.a[j++] = (byte) (c0 & 255);
                packetplayoutmapchunk_chunkmap.a[j++] = (byte) (c0 >> 8 & 255);
            }
        }

        Iterator<ChunkSection> iterator;
        ChunkSection chunksection;

        for (iterator = arraylist.iterator(); iterator.hasNext(); j = a(chunksection.getEmittedLightArray().a(), packetplayoutmapchunk_chunkmap.a, j)) {
            chunksection = iterator.next();
        }

        if (flag1) {
            for (iterator = arraylist.iterator(); iterator.hasNext(); j = a(chunksection.getSkyLightArray().a(), packetplayoutmapchunk_chunkmap.a, j)) {
                chunksection = iterator.next();
            }
        }

        if (flag) {
            a(chunk.getBiomeIndex(), packetplayoutmapchunk_chunkmap.a, j);
        }

        return packetplayoutmapchunk_chunkmap;
    }

    private static int a(byte[] abyte, byte[] abyte1, int i) {
        System.arraycopy(abyte, 0, abyte1, i, abyte.length);
        return i + abyte.length;
    }

    public static class ChunkMap {

        public byte[] a;
        public int b;

        public ChunkMap() {
        }
    }
}
