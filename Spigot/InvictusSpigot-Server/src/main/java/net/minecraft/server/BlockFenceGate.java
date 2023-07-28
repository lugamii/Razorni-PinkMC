package net.minecraft.server;

public class BlockFenceGate extends BlockDirectional {

    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateBoolean IN_WALL = BlockStateBoolean.of("in_wall");

    public BlockFenceGate(BlockWood.EnumLogVariant blockwood_enumlogvariant) {
        super(Material.WOOD, blockwood_enumlogvariant.c());
        this.j(this.blockStateList.getBlockData().set(BlockFenceGate.OPEN, Boolean.FALSE).set(BlockFenceGate.POWERED, Boolean.FALSE).set(BlockFenceGate.IN_WALL, Boolean.FALSE));
        this.a(CreativeModeTab.d);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = iblockdata.get(BlockFenceGate.FACING).k();

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z && (iblockaccess.getType(blockposition.west()).getBlock() == Blocks.COBBLESTONE_WALL || iblockaccess.getType(blockposition.east()).getBlock() == Blocks.COBBLESTONE_WALL) || enumdirection_enumaxis == EnumDirection.EnumAxis.X && (iblockaccess.getType(blockposition.north()).getBlock() == Blocks.COBBLESTONE_WALL || iblockaccess.getType(blockposition.south()).getBlock() == Blocks.COBBLESTONE_WALL)) {
            iblockdata = iblockdata.set(BlockFenceGate.IN_WALL, Boolean.TRUE);
        }

        return iblockdata;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock().getMaterial().isBuildable() && super.canPlace(world, blockposition);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.get(BlockFenceGate.OPEN)) {
            return null;
        } else {
            EnumDirection.EnumAxis enumdirection_enumaxis = iblockdata.get(BlockFenceGate.FACING).k();

            return enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? new AxisAlignedBB(blockposition.getX(), blockposition.getY(), (float) blockposition.getZ() + 0.375F, blockposition.getX() + 1, (float) blockposition.getY() + 1.5F, (float) blockposition.getZ() + 0.625F) : new AxisAlignedBB((float) blockposition.getX() + 0.375F, blockposition.getY(), blockposition.getZ(), (float) blockposition.getX() + 0.625F, (float) blockposition.getY() + 1.5F, blockposition.getZ() + 1);
        }
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = iblockaccess.getType(blockposition).get(BlockFenceGate.FACING).k();

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z) {
            this.a(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        } else {
            this.a(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }

    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition).get(BlockFenceGate.OPEN);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockFenceGate.FACING, entityliving.getDirection()).set(BlockFenceGate.OPEN, Boolean.FALSE).set(BlockFenceGate.POWERED, Boolean.FALSE).set(BlockFenceGate.IN_WALL, Boolean.FALSE);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (iblockdata.get(BlockFenceGate.OPEN)) {
            iblockdata = iblockdata.set(BlockFenceGate.OPEN, Boolean.FALSE);
        } else {
            EnumDirection enumdirection1 = EnumDirection.fromAngle(entityhuman.yaw);

            if (iblockdata.get(BlockFenceGate.FACING) == enumdirection1.opposite()) {
                iblockdata = iblockdata.set(BlockFenceGate.FACING, enumdirection1);
            }

            iblockdata = iblockdata.set(BlockFenceGate.OPEN, Boolean.TRUE);
        }
        world.setTypeAndData(blockposition, iblockdata, 2);

        world.a(entityhuman, iblockdata.get(BlockFenceGate.OPEN) ? 1003 : 1006, blockposition, 0);
        return true;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        boolean flag = world.isBlockIndirectlyPowered(blockposition);

        if (flag || block.isPowerSource()) {
            if (flag && !iblockdata.get(BlockFenceGate.OPEN) && !iblockdata.get(BlockFenceGate.POWERED)) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockFenceGate.OPEN, Boolean.TRUE).set(BlockFenceGate.POWERED, Boolean.TRUE), 2);
                world.a(null, 1003, blockposition, 0);
            } else if (!flag && iblockdata.get(BlockFenceGate.OPEN) && iblockdata.get(BlockFenceGate.POWERED)) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockFenceGate.OPEN, Boolean.FALSE).set(BlockFenceGate.POWERED, Boolean.FALSE), 2);
                world.a(null, 1006, blockposition, 0);
            } else if (flag != iblockdata.get(BlockFenceGate.POWERED)) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockFenceGate.POWERED, flag), 2);
            }
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFenceGate.FACING, EnumDirection.fromType2(i)).set(BlockFenceGate.OPEN, (i & 4) != 0).set(BlockFenceGate.POWERED, (i & 8) != 0);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockFenceGate.FACING).b();

        if (iblockdata.get(BlockFenceGate.POWERED)) {
            i |= 8;
        }

        if (iblockdata.get(BlockFenceGate.OPEN)) {
            i |= 4;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockFenceGate.FACING, BlockFenceGate.OPEN, BlockFenceGate.POWERED, BlockFenceGate.IN_WALL);
    }
}
