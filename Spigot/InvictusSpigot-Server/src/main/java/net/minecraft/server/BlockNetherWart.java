package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockPlant {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 3);

    protected BlockNetherWart() {
        super(Material.PLANT, MaterialMapColor.D);
        this.j(this.blockStateList.getBlockData().set(BlockNetherWart.AGE, 0));
        this.a(true);
        float f = 0.5F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.a((CreativeModeTab) null);
    }

    protected boolean c(Block block) {
        return block == Blocks.SOUL_SAND;
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return this.c(world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock());
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        int i = iblockdata.get(BlockNetherWart.AGE);

        if (i < 3 && random.nextInt(Math.max(1, (int) world.growthOdds / world.spigotConfig.wartModifier * 10)) == 0) { // Spigot
            iblockdata = iblockdata.set(BlockNetherWart.AGE, i + 1);
            // world.setTypeAndData(blockposition, iblockdata, 2); // CraftBukkit
            org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, toLegacyData(iblockdata)); // CraftBukkit
        }

        super.b(world, blockposition, iblockdata, random);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        int j = 1;

        if (iblockdata.get(BlockNetherWart.AGE) >= 3) {
            j = 2 + world.random.nextInt(3);
            if (i > 0) {
                j += world.random.nextInt(i + 1);
            }
        }

        for (int k = 0; k < j; ++k) {
            a(world, blockposition, new ItemStack(Items.NETHER_WART));
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return null;
    }

    public int a(Random random) {
        return 0;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockNetherWart.AGE, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockNetherWart.AGE);
    }

    public BlockStateList getStateList() {
        return new BlockStateList(this, BlockNetherWart.AGE);
    }
}
