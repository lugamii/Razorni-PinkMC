package net.minecraft.server;

public interface IBlockAccess {

    TileEntity getTileEntity(BlockPosition blockposition);

    IBlockData getType(BlockPosition blockposition);

    default IBlockData getType(int x, int y, int z) {
        return getType(new BlockPosition(x, y, z));
    }

    boolean isEmpty(BlockPosition blockposition);

    int getBlockPower(BlockPosition blockposition, EnumDirection enumdirection);
}
