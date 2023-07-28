package net.minecraft.server;

import java.util.Random;

public class WorldGenVines extends WorldGenerator {

    public WorldGenVines() {
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        for (; blockposition.getY() < 128; blockposition = blockposition.up()) {
            if (world.isEmpty(blockposition)) {
                EnumDirection[] aenumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a();
                int i = aenumdirection.length;

                for (EnumDirection enumdirection : aenumdirection) {
                    if (Blocks.VINE.canPlace(world, blockposition, enumdirection)) {
                        IBlockData iblockdata = Blocks.VINE.getBlockData().set(BlockVine.NORTH, enumdirection == EnumDirection.NORTH).set(BlockVine.EAST, enumdirection == EnumDirection.EAST).set(BlockVine.SOUTH, enumdirection == EnumDirection.SOUTH).set(BlockVine.WEST, enumdirection == EnumDirection.WEST);

                        world.setTypeAndData(blockposition, iblockdata, 2);
                        break;
                    }
                }
            } else {
                blockposition = blockposition.a(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            }
        }

        return true;
    }
}
