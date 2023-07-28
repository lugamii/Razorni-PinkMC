package net.minecraft.server;

import java.util.Random;

public class WorldGenTaiga1 extends WorldGenTreeAbstract {

    private static final IBlockData a = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.SPRUCE);
    private static final IBlockData b = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.SPRUCE).set(BlockLeaves.CHECK_DECAY, Boolean.FALSE);

    public WorldGenTaiga1() {
        super(false);
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        int i = random.nextInt(5) + 7;
        int j = i - random.nextInt(2) - 3;
        int k = i - j;
        int l = 1 + random.nextInt(k + 1);
        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            int i1;
            int j1;
            int k1;

            for (int l1 = blockposition.getY(); l1 <= blockposition.getY() + 1 + i && flag; ++l1) {

                if (l1 - blockposition.getY() < j) {
                    k1 = 0;
                } else {
                    k1 = l;
                }

                for (i1 = blockposition.getX() - k1; i1 <= blockposition.getX() + k1 && flag; ++i1) {
                    for (j1 = blockposition.getZ() - k1; j1 <= blockposition.getZ() + k1 && flag; ++j1) {
                        if (l1 >= 0 && l1 < 256) {
                            if (!this.a(world.getType(i1, l1, j1).getBlock())) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();

                if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockposition.getY() < 256 - i - 1) {
                    this.a(world, blockposition.down());
                    k1 = 0;

                    int i2;

                    for (i2 = blockposition.getY() + i; i2 >= blockposition.getY() + j; --i2) {
                        for (i1 = blockposition.getX() - k1; i1 <= blockposition.getX() + k1; ++i1) {
                            j1 = i1 - blockposition.getX();

                            for (int j2 = blockposition.getZ() - k1; j2 <= blockposition.getZ() + k1; ++j2) {
                                int k2 = j2 - blockposition.getZ();

                                if (Math.abs(j1) != k1 || Math.abs(k2) != k1 || k1 <= 0) {
                                    if (!world.getType(i1, i2, j2).getBlock().o()) {
                                        this.a(world, new BlockPosition(i1, i2, j2), WorldGenTaiga1.b);
                                    }
                                }
                            }
                        }

                        if (k1 >= 1 && i2 == blockposition.getY() + j + 1) {
                            --k1;
                        } else if (k1 < l) {
                            ++k1;
                        }
                    }

                    for (i2 = 0; i2 < i - 1; ++i2) {
                        Block block1 = world.getType(blockposition.up(i2)).getBlock();

                        if (block1.getMaterial() == Material.AIR || block1.getMaterial() == Material.LEAVES) {
                            this.a(world, blockposition.up(i2), WorldGenTaiga1.a);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
