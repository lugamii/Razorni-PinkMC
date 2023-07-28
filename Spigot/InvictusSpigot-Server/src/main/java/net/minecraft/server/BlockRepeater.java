package net.minecraft.server;

import java.util.Random;

public class BlockRepeater extends BlockDiodeAbstract {

    public static final BlockStateBoolean LOCKED = BlockStateBoolean.of("locked");
    public static final BlockStateInteger DELAY = BlockStateInteger.of("delay", 1, 4);

    protected BlockRepeater(boolean flag) {
        super(flag);
        this.j(this.blockStateList.getBlockData().set(BlockRepeater.FACING, EnumDirection.NORTH).set(BlockRepeater.DELAY, 1).set(BlockRepeater.LOCKED, false));
    }

    public String getName() {
        return LocaleI18n.get("item.diode.name");
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(LOCKED, this.b(iblockaccess, blockposition, iblockdata));
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.abilities.mayBuild) {
            return false;
        } else {
            world.setTypeAndData(blockposition, iblockdata.a(BlockRepeater.DELAY), 3, false, false);
            return true;
        }
    }

    protected int d(IBlockData iblockdata) {
        return iblockdata.get(BlockRepeater.DELAY) * 2;
    }

    protected IBlockData e(IBlockData iblockdata) {
        return Blocks.POWERED_REPEATER.getBlockData().set(FACING, iblockdata.get(FACING)).set(DELAY, iblockdata.get(DELAY)).set(LOCKED, iblockdata.get(BlockRepeater.LOCKED));
    }

    protected IBlockData k(IBlockData iblockdata) {
        return Blocks.UNPOWERED_REPEATER.getBlockData().set(FACING, iblockdata.get(FACING)).set(DELAY, iblockdata.get(DELAY)).set(LOCKED, iblockdata.get(BlockRepeater.LOCKED));
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REPEATER;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return c(iblockaccess, blockposition, iblockdata) > 0;
    }

    protected boolean c(Block block) {
        return d(block);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        this.h(world, blockposition, iblockdata);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockRepeater.FACING, EnumDirection.fromType2(i)).set(BlockRepeater.LOCKED, false).set(BlockRepeater.DELAY, 1 + (i >> 2));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockRepeater.FACING).b();

        i |= iblockdata.get(BlockRepeater.DELAY) - 1 << 2;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockRepeater.FACING, BlockRepeater.DELAY, BlockRepeater.LOCKED);
    }
}
