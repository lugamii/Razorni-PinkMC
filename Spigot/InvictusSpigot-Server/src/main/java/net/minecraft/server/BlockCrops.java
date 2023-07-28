package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.Random;

public class BlockCrops extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 7);

    protected BlockCrops() {
        this.j(this.blockStateList.getBlockData().set(BlockCrops.AGE, 0));
        this.a(true);
        float f = 0.5F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.a((CreativeModeTab) null);
        this.c(0.0F);
        this.a(BlockCrops.h);
        this.K();
    }

    protected static float a(Block block, World world, BlockPosition blockposition) {
        float f = 1.0F;
        BlockPosition blockposition1 = blockposition.down();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                IBlockData iblockdata = world.getType(blockposition1.a(i, 0, j));

                if (iblockdata.getBlock() == Blocks.FARMLAND) {
                    f1 = 1.0F;
                    if (iblockdata.get(BlockSoil.MOISTURE) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }
        
        boolean flag = block == world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ()).getBlock()
                || block == world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ()).getBlock();
        boolean flag1 = block == world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1).getBlock()
                || block == world.getType(blockposition.getX(), blockposition.getY() , blockposition.getZ() + 1).getBlock();

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = block == world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ() - 1).getBlock()
                    || block == world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ() - 1).getBlock()
                    || block == world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ() + 1).getBlock()
                    || block == world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ() + 1).getBlock();

            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    protected boolean c(Block block) {
        return block == Blocks.FARMLAND;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (InvictusConfig.dropFloatingPlants) {
            super.b(world, blockposition, iblockdata, random);
        } else if (!f(world, blockposition, iblockdata)) {
            return;
        }
        if (world.getLightLevel(blockposition.up()) >= 9) {
            int i = iblockdata.get(BlockCrops.AGE);

            if (i < 7) {
                float f = a(this, world, blockposition);

                if (random
                        .nextInt((int) (world.growthOdds / world.spigotConfig.wheatModifier * (25.0F / f)) + 1) == 0) { // Spigot
                    // //
                    // CraftBukkit
                    // start
                    IBlockData data = iblockdata.set(AGE, i + 1);
                    CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(),
                            blockposition.getZ(), this, toLegacyData(data));
                    // CraftBukkit end
                }
            }
        }

    }

    public void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = iblockdata.get(BlockCrops.AGE) + MathHelper.nextInt(world.random, 2, 5);

        if (i > 7) {
            i = 7;
        }

        // CraftBukkit start

        CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(),
                this, toLegacyData(iblockdata.set(AGE, i)));
        // CraftBukkit end
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return (world.k(blockposition) >= 8 || world.i(blockposition))
                && this.c(world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock());
    }

    protected Item l() {
        return Items.WHEAT_SEEDS;
    }

    protected Item n() {
        return Items.WHEAT;
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        super.dropNaturally(world, blockposition, iblockdata, f, 0);
        int j = iblockdata.get(BlockCrops.AGE);

        if (j >= 7) {
            int k = 3 + i;

            for (int l = 0; l < k; ++l) {
                if (world.random.nextInt(15) <= j) {
                    a(world, blockposition, new ItemStack(this.l(), 1, 0));
                }
            }
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return iblockdata.get(BlockCrops.AGE) == 7 ? this.n() : this.l();
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockdata.get(BlockCrops.AGE) < 7;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.g(world, blockposition, iblockdata);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCrops.AGE, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockCrops.AGE);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockCrops.AGE);
    }
}
