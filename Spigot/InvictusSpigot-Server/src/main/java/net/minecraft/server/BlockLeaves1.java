package net.minecraft.server;

public class BlockLeaves1 extends BlockLeaves {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.a("variant", BlockWood.EnumLogVariant.class, blockwood_enumlogvariant -> blockwood_enumlogvariant.a() < 4);

    public BlockLeaves1() {
        this.j(this.blockStateList.getBlockData().set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.OAK).set(BlockLeaves1.CHECK_DECAY, Boolean.TRUE).set(BlockLeaves1.DECAYABLE, Boolean.TRUE));
    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        if (iblockdata.get(BlockLeaves1.VARIANT) == BlockWood.EnumLogVariant.OAK && world.random.nextInt(i) == 0) {
            a(world, blockposition, new ItemStack(Items.APPLE, 1, 0));
        }

    }

    protected int d(IBlockData iblockdata) {
        return iblockdata.get(BlockLeaves1.VARIANT) == BlockWood.EnumLogVariant.JUNGLE ? 40 : super.d(iblockdata);
    }

    protected ItemStack i(IBlockData iblockdata) {
        return new ItemStack(Item.getItemOf(this), 1, iblockdata.get(BlockLeaves1.VARIANT).a());
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockLeaves1.VARIANT, this.b(i)).set(BlockLeaves1.DECAYABLE, (i & 4) == 0).set(BlockLeaves1.CHECK_DECAY, (i & 8) > 0);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockLeaves1.VARIANT).a();

        if (!iblockdata.get(BlockLeaves1.DECAYABLE)) {
            i |= 4;
        }

        if (iblockdata.get(BlockLeaves1.CHECK_DECAY)) {
            i |= 8;
        }

        return i;
    }

    public BlockWood.EnumLogVariant b(int i) {
        return BlockWood.EnumLogVariant.a((i & 3) % 4);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockLeaves1.VARIANT, BlockLeaves1.CHECK_DECAY, BlockLeaves1.DECAYABLE);
    }

    public int getDropData(IBlockData iblockdata) {
        return iblockdata.get(BlockLeaves1.VARIANT).a();
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
            a(world, blockposition, new ItemStack(Item.getItemOf(this), 1, iblockdata.get(BlockLeaves1.VARIANT).a()));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity);
        }
    }
}
