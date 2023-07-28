package net.minecraft.server;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenMinable extends WorldGenerator {

    private final IBlockData a;
    private final int b;
    private final Predicate<IBlockData> c;
    private boolean mustTouchAir;

    public WorldGenMinable(IBlockData iblockdata, int i) {
        this(iblockdata, i, BlockPredicate.a(Blocks.STONE));
    }

    public WorldGenMinable(IBlockData iblockdata, int i, Predicate<IBlockData> predicate) {
        this.a = iblockdata;
        this.b = i;
        this.c = predicate;
    }

    public WorldGenMinable(IBlockData block, int size, boolean mustTouchAir) {
        this(block, size);
        this.mustTouchAir = mustTouchAir;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        float f = random.nextFloat() * 3.1415927F;
        double d0 = (float) (blockposition.getX() + 8) + MathHelper.sin(f) * (float) this.b / 8.0F;
        double d1 = (float) (blockposition.getX() + 8) - MathHelper.sin(f) * (float) this.b / 8.0F;
        double d2 = (float) (blockposition.getZ() + 8) + MathHelper.cos(f) * (float) this.b / 8.0F;
        double d3 = (float) (blockposition.getZ() + 8) - MathHelper.cos(f) * (float) this.b / 8.0F;
        double d4 = blockposition.getY() + random.nextInt(3) - 2;
        double d5 = blockposition.getY() + random.nextInt(3) - 2;

        boolean touchedAir = false;
        List<BlockPosition> blocks = mustTouchAir ? new ArrayList<>() : null;

        for (int i = 0; i < this.b; ++i) {
            float f1 = (float) i / (float) this.b;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = random.nextDouble() * (double) this.b / 16.0D;
            double d10 = (double) (MathHelper.sin(3.1415927F * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin(3.1415927F * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPosition blockposition1 = new BlockPosition(l1, i2, j2);

                                    if (this.c.apply(world.getType(blockposition1))) {
                                        if (mustTouchAir) {
                                            blocks.add(blockposition1);
                                            touchedAir |=
                                                    world.getType(l1 + 1, i2, j2).getBlock() == Blocks.AIR ||
                                                            world.getType(l1 - 1, i2, j2).getBlock() == Blocks.AIR ||
                                                            world.getType(l1, i2 + 1, j2).getBlock() == Blocks.AIR ||
                                                            world.getType(l1, i2 - 1, j2).getBlock() == Blocks.AIR ||
                                                            world.getType(l1, i2, j2 + 1).getBlock() == Blocks.AIR ||
                                                            world.getType(l1, i2, j2 - 1).getBlock() == Blocks.AIR;
                                        } else {
                                            world.setTypeAndData(blockposition1, this.a, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mustTouchAir && touchedAir) {
            for (BlockPosition pos : blocks) {
                world.setTypeAndData(pos, this.a, 2);
            }
        }

        return true;
    }
}
