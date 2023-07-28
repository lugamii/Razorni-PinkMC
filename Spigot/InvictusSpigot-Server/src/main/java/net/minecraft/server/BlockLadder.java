package net.minecraft.server;

public class BlockLadder extends Block {
    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);

    protected BlockLadder() {
        super(Material.ORIENTABLE);
        j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
        a(CreativeModeTab.c);
    }

    public AxisAlignedBB a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
        updateShape(paramWorld, paramBlockPosition);
        return super.a(paramWorld, paramBlockPosition, paramIBlockData);
    }

    public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
        IBlockData iBlockData = paramIBlockAccess.getType(paramBlockPosition);
        if (iBlockData.getBlock() != this)
            return;
        switch (SyntheticClass_1.a[iBlockData.get(BlockLadder.FACING).ordinal()]) {
            case 1:
                a(0.0F, 0.0F, 1.0F - 0.125F, 1.0F, 1.0F, 1.0F);
                return;
            case 2:
                a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
                return;
            case 3:
                a(1.0F - 0.125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                return;
        }
        a(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World world, BlockPosition pos) {
        return world.getType(pos.getX(), pos.getY(), pos.getZ() - 1).getBlock().isOccluding() || world.getType(pos.getX() + 1, pos.getY(), pos.getZ()).getBlock().isOccluding() || world.getType(pos.getX() - 1, pos.getY(), pos.getZ()).getBlock().isOccluding() || world.getType(pos.getX(), pos.getY(), pos.getZ() + 1).getBlock().isOccluding();
    }

    public IBlockData getPlacedState(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, EntityLiving paramEntityLiving) {
        if (paramEnumDirection.k().c() && a(paramWorld, paramBlockPosition, paramEnumDirection))
            return getBlockData().set(FACING, paramEnumDirection);
        for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (a(paramWorld, paramBlockPosition, enumDirection))
                return getBlockData().set(FACING, enumDirection);
        }
        return getBlockData();
    }

    public void doPhysics(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, Block paramBlock) {
        EnumDirection enumDirection = paramIBlockData.get(FACING);
        if (!a(paramWorld, paramBlockPosition, enumDirection)) {
            b(paramWorld, paramBlockPosition, paramIBlockData, 0);
            paramWorld.setAir(paramBlockPosition);
        }
        super.doPhysics(paramWorld, paramBlockPosition, paramIBlockData, paramBlock);
    }

    protected boolean a(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection) {
        return paramWorld.getType(paramBlockPosition.shift(paramEnumDirection.opposite())).getBlock().isOccluding();
    }

    public IBlockData fromLegacyData(int paramInt) {
        EnumDirection enumDirection = EnumDirection.fromType1(paramInt);
        if (enumDirection.k() == EnumDirection.EnumAxis.Y)
            enumDirection = EnumDirection.NORTH;
        return getBlockData().set(FACING, enumDirection);
    }

    public int toLegacyData(IBlockData paramIBlockData) {
        return paramIBlockData.get(FACING).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, FACING);
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }
}
