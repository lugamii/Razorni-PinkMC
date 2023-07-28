package net.minecraft.server;

public class BlockLeaves2 extends BlockLeaves {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.a("variant", BlockWood.EnumLogVariant.class, blockwood_enumlogvariant -> blockwood_enumlogvariant.a() >= 4);

    public BlockLeaves2() {
        this.j(this.blockStateList.getBlockData().set(BlockLeaves2.VARIANT, BlockWood.EnumLogVariant.ACACIA).set(BlockLeaves2.CHECK_DECAY, Boolean.TRUE).set(BlockLeaves2.DECAYABLE, Boolean.valueOf(true)));
    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        if (iblockdata.get(BlockLeaves2.VARIANT) == BlockWood.EnumLogVariant.DARK_OAK && world.random.nextInt(i) == 0) {
            a(world, blockposition, new ItemStack(Items.APPLE, 1, 0));
        }

    }

    public int getDropData(IBlockData iblockdata) {
        return iblockdata.get(BlockLeaves2.VARIANT).a();
    }

    public int getDropData(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.getBlock().toLegacyData(iblockdata) & 3;
    }

    protected ItemStack i(IBlockData iblockdata) {
        return new ItemStack(Item.getItemOf(this), 1, iblockdata.get(BlockLeaves2.VARIANT).a() - 4);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockLeaves2.VARIANT, this.b(i)).set(BlockLeaves2.DECAYABLE, (i & 4) == 0).set(BlockLeaves2.CHECK_DECAY, (i & 8) > 0);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockLeaves2.VARIANT).a() - 4;

        if (!iblockdata.get(BlockLeaves2.DECAYABLE)) {
            i |= 4;
        }

        if (iblockdata.get(BlockLeaves2.CHECK_DECAY)) {
            i |= 8;
        }

        return i;
    }

    public BlockWood.EnumLogVariant b(int i) {
        return BlockWood.EnumLogVariant.a((i & 3) + 4);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockLeaves2.VARIANT, BlockLeaves2.CHECK_DECAY, BlockLeaves2.DECAYABLE);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
            a(world, blockposition, new ItemStack(Item.getItemOf(this), 1, iblockdata.get(BlockLeaves2.VARIANT).a() - 4));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity);
        }
    }
}
