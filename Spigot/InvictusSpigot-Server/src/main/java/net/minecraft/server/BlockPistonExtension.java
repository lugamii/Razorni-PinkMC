package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockPistonExtension extends Block {
    public static final BlockStateDirection FACING = BlockStateDirection.of("facing");

    public static final BlockStateEnum<EnumPistonType> TYPE = BlockStateEnum.of("type", EnumPistonType.class);

    public static final BlockStateBoolean SHORT = BlockStateBoolean.of("short");

    public BlockPistonExtension() {
        super(Material.PISTON);
        j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(TYPE, EnumPistonType.DEFAULT).set(SHORT, Boolean.FALSE));
        a(i);
        c(0.5F);
    }

    public static EnumDirection b(int paramInt) {
        int i = paramInt & 0x7;
        if (i > 5)
            return null;
        return EnumDirection.fromType1(i);
    }

    public void a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, EntityHuman paramEntityHuman) {
        if (paramEntityHuman.abilities.canInstantlyBuild) {
            EnumDirection enumDirection = paramIBlockData.get(FACING);
            if (enumDirection != null) {
                BlockPosition blockPosition = paramBlockPosition.shift(enumDirection.opposite());
                Block block = paramWorld.getType(blockPosition).getBlock();
                if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON)
                    paramWorld.setAir(blockPosition);
            }
        }
        super.a(paramWorld, paramBlockPosition, paramIBlockData, paramEntityHuman);
    }

    public void remove(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
        super.remove(paramWorld, paramBlockPosition, paramIBlockData);
        EnumDirection enumDirection = paramIBlockData.get(FACING).opposite();
        paramBlockPosition = paramBlockPosition.shift(enumDirection);
        IBlockData iBlockData = paramWorld.getType(paramBlockPosition);
        if ((iBlockData.getBlock() == Blocks.PISTON || iBlockData.getBlock() == Blocks.STICKY_PISTON) && iBlockData.<Boolean>get(BlockPiston.EXTENDED)) {
            iBlockData.getBlock().b(paramWorld, paramBlockPosition, iBlockData, 0);
            paramWorld.setAir(paramBlockPosition);
        }
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition) {
        return false;
    }

    public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection) {
        return false;
    }

    public int a(Random paramRandom) {
        return 0;
    }

    public void a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, AxisAlignedBB paramAxisAlignedBB, List<AxisAlignedBB> paramList, Entity paramEntity) {
        d(paramIBlockData);
        super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
        e(paramIBlockData);
        super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private void e(IBlockData paramIBlockData) {
        switch (BlockPistonExtension.SyntheticClass_1.a[(paramIBlockData.get(FACING)).ordinal()]) {
            case 1:
                a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                break;
            case 2:
                a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                break;
            case 3:
                a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                break;
            case 4:
                a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                break;
            case 5:
                a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                break;
            case 6:
                a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                break;
        }
    }

    public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
        d(paramIBlockAccess.getType(paramBlockPosition));
    }

    public void d(IBlockData paramIBlockData) {
        EnumDirection enumDirection = paramIBlockData.get(FACING);
        if (enumDirection == null)
            return;
        switch (BlockPistonExtension.SyntheticClass_1.a[enumDirection.ordinal()]) {
            case 1:
                a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 2:
                a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 3:
                a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 4:
                a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 5:
                a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 6:
                a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
        }
    }

    public void doPhysics(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, Block paramBlock) {
        EnumDirection enumDirection = paramIBlockData.get(FACING);
        BlockPosition blockPosition = paramBlockPosition.shift(enumDirection.opposite());
        IBlockData iBlockData = paramWorld.getType(blockPosition);
        if (iBlockData.getBlock() != Blocks.PISTON && iBlockData.getBlock() != Blocks.STICKY_PISTON) {
            paramWorld.setAir(paramBlockPosition);
        } else {
            iBlockData.getBlock().doPhysics(paramWorld, blockPosition, iBlockData, paramBlock);
        }
    }

    public IBlockData fromLegacyData(int paramInt) {
        return getBlockData().set(FACING, b(paramInt)).set(TYPE, ((paramInt & 0x8) > 0) ? EnumPistonType.STICKY : EnumPistonType.DEFAULT);
    }

    public int toLegacyData(IBlockData paramIBlockData) {
        int i = 0;
        i |= paramIBlockData.get(FACING).a();
        if (paramIBlockData.get(TYPE) == EnumPistonType.STICKY)
            i |= 0x8;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, FACING, TYPE, SHORT);
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.DOWN.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockPistonExtension.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 6;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }

    public enum EnumPistonType implements INamable {
        DEFAULT("normal"),
        STICKY("sticky");

        private final String c;

        EnumPistonType(String param1String1) {
            this.c = param1String1;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
