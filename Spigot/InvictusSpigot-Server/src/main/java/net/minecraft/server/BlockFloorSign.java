package net.minecraft.server;

public class BlockFloorSign extends BlockSign {

    public static final BlockStateInteger ROTATION = BlockStateInteger.of("rotation", 0, 15);

    public BlockFloorSign() {
        this.j(this.blockStateList.getBlockData().set(BlockFloorSign.ROTATION, 0));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.getType(blockposition.down()).getBlock().getMaterial().isBuildable()) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

        super.doPhysics(world, blockposition, iblockdata, block);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFloorSign.ROTATION, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockFloorSign.ROTATION);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockFloorSign.ROTATION);
    }
}
