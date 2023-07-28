package net.minecraft.server;

import java.util.List;
import java.util.Random;

public abstract class BlockStepAbstract extends Block {
    public static final BlockStateEnum<EnumSlabHalf> HALF = BlockStateEnum.of("half", EnumSlabHalf.class);

    public BlockStepAbstract(Material paramMaterial) {
        super(paramMaterial);
        if (l()) {
            this.r = true;
        } else {
            a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        e(255);
    }

    protected boolean I() {
        return false;
    }

    public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
        if (l()) {
            a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        IBlockData iBlockData = paramIBlockAccess.getType(paramBlockPosition);
        if (iBlockData.getBlock() == this)
            if (iBlockData.get(HALF) == EnumSlabHalf.TOP) {
                a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
    }

    public void j() {
        if (l()) {
            a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    public void a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, AxisAlignedBB paramAxisAlignedBB, List<AxisAlignedBB> paramList, Entity paramEntity) {
        updateShape(paramWorld, paramBlockPosition);
        super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
    }

    public boolean c() {
        return l();
    }

    public IBlockData getPlacedState(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, EntityLiving paramEntityLiving) {
        IBlockData iBlockData = super.getPlacedState(paramWorld, paramBlockPosition, paramEnumDirection, paramFloat1, paramFloat2, paramFloat3, paramInt, paramEntityLiving).set(HALF, EnumSlabHalf.BOTTOM);
        if (l())
            return iBlockData;
        if (paramEnumDirection == EnumDirection.DOWN || (paramEnumDirection != EnumDirection.UP && paramFloat2 > 0.5D))
            return iBlockData.set(HALF, EnumSlabHalf.TOP);
        return iBlockData;
    }

    public int a(Random paramRandom) {
        if (l())
            return 2;
        return 1;
    }

    public boolean d() {
        return l();
    }

    public abstract String b(int paramInt);

    public int getDropData(World paramWorld, BlockPosition paramBlockPosition) {
        return super.getDropData(paramWorld, paramBlockPosition) & 0x7;
    }

    public abstract boolean l();

    public abstract IBlockState<?> n();

    public abstract Object a(ItemStack paramItemStack);

    public enum EnumSlabHalf implements INamable {
        TOP("top"),
        BOTTOM("bottom");

        private final String c;

        EnumSlabHalf(String param1String1) {
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
