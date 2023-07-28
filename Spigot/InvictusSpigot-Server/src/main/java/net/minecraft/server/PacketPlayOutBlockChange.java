package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutBlockChange implements Packet<PacketListenerPlayOut> {

    public IBlockData block;
    private BlockPosition a;

    public PacketPlayOutBlockChange() {
    }

    public PacketPlayOutBlockChange(World world, BlockPosition blockposition) {
        this.a = blockposition;
        this.block = world.getType(blockposition);
    }

    public PacketPlayOutBlockChange(World world, Chunk chunk, BlockPosition blockposition) {
        this.a = blockposition;
        this.block = world.getType(chunk, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
    }

    public PacketPlayOutBlockChange(BlockPosition blockposition, IBlockData block) {
        this.a = blockposition;
        this.block = block;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c();
        this.block = Block.d.a(packetdataserializer.e());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.b(Block.d.b(this.block));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

}
