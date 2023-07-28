package net.minecraft.server;

import java.util.Random;

public class WorldGenEnder extends WorldGenerator {

    private Block a;

    public WorldGenEnder(Block block) {
        this.a = block;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        if (world.isEmpty(blockposition) && world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() == this.a) {
            int i = random.nextInt(32) + 6;
            int j = random.nextInt(4) + 1;

            int k;
            int l;
            int i1;
            int j1;

            for (k = blockposition.getX() - j; k <= blockposition.getX() + j; ++k) {
                for (l = blockposition.getZ() - j; l <= blockposition.getZ() + j; ++l) {
                    i1 = k - blockposition.getX();
                    j1 = l - blockposition.getZ();
                    if (i1 * i1 + j1 * j1 <= j * j + 1 && world.getType(k, blockposition.getY() - 1, l).getBlock() != this.a) {
                        return false;
                    }
                }
            }

            for (k = blockposition.getY(); k < blockposition.getY() + i && k < 256; ++k) {
                for (l = blockposition.getX() - j; l <= blockposition.getX() + j; ++l) {
                    for (i1 = blockposition.getZ() - j; i1 <= blockposition.getZ() + j; ++i1) {
                        j1 = l - blockposition.getX();
                        int k1 = i1 - blockposition.getZ();

                        if (j1 * j1 + k1 * k1 <= j * j + 1) {
                            world.setTypeAndData(new BlockPosition(l, k, i1), Blocks.OBSIDIAN.getBlockData(), 2);
                        }
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world);

            entityendercrystal.setPositionRotation((float) blockposition.getX() + 0.5F, blockposition.getY() + i, (float) blockposition.getZ() + 0.5F, random.nextFloat() * 360.0F, 0.0F);
            world.addEntity(entityendercrystal);
            world.setTypeAndData(blockposition.up(i), Blocks.BEDROCK.getBlockData(), 2);
            return true;
        } else {
            return false;
        }
    }
}
