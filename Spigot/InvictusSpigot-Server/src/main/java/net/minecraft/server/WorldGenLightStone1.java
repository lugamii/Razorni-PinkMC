package net.minecraft.server;

import java.util.Random;

public class WorldGenLightStone1 extends WorldGenerator {

    public WorldGenLightStone1() {
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        if (!world.isEmpty(blockposition)) {
            return false;
        } else if (world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock() != Blocks.NETHERRACK) {
            return false;
        } else {
            world.setTypeAndData(blockposition, Blocks.GLOWSTONE.getBlockData(), 2);

            for (int i = 0; i < 1500; ++i) {
                BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));

                if (world.getType(blockposition1).getBlock().getMaterial() == Material.AIR) {
                    int j = 0;

                    for (EnumDirection enumdirection : EnumDirection.values()) {
                        if (world.getType(blockposition1.getX() + enumdirection.getAdjacentX(), blockposition1.getY() + enumdirection.getAdjacentY(), blockposition1.getZ() + enumdirection.getAdjacentZ()).getBlock() == Blocks.GLOWSTONE) {
                            ++j;
                        }

                        if (j > 1) {
                            break;
                        }
                    }

                    if (j == 1) {
                        world.setTypeAndData(blockposition1, Blocks.GLOWSTONE.getBlockData(), 2);
                    }
                }
            }

            return true;
        }
    }
}
