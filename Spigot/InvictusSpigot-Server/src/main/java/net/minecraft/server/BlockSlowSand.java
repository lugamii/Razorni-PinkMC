package net.minecraft.server;

public class BlockSlowSand extends Block {

    public BlockSlowSand() {
        super(Material.SAND, MaterialMapColor.B);
        this.a(CreativeModeTab.b);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new AxisAlignedBB(blockposition.getX(), blockposition.getY(), blockposition.getZ(), (blockposition.getX() + 1), (float) (blockposition.getY() + 1) - 0.125F, (blockposition.getZ() + 1));
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        entity.motX *= 0.4D;
        entity.motZ *= 0.4D;
    }
}
