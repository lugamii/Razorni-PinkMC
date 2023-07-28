package net.minecraft.server;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator {

    private final Block a;

    public WorldGenLiquids(Block block) {
        this.a = block;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        if (world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock() != Blocks.STONE) {
            return false;
        } else if (world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() != Blocks.STONE) {
            return false;
        } else if (world.getType(blockposition).getBlock().getMaterial() != Material.AIR && world.getType(blockposition).getBlock() != Blocks.STONE) {
            return false;
        } else {
            int i = 0;

            if (world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ()).getBlock() == Blocks.STONE) {
                ++i;
            }

            if (world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ()).getBlock() == Blocks.STONE) {
                ++i;
            }

            if (world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1).getBlock() == Blocks.STONE) {
                ++i;
            }

            if (world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1).getBlock() == Blocks.STONE) {
                ++i;
            }

            int j = 0;

            if (world.isEmpty(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ())) {
                ++j;
            }

            if (world.isEmpty(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ())) {
                ++j;
            }

            if (world.isEmpty(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1)) {
                ++j;
            }

            if (world.isEmpty(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1)) {
                ++j;
            }

            if (i == 3 && j == 1) {
                world.setTypeAndData(blockposition, this.a.getBlockData(), 2);
                world.a(this.a, blockposition, random);
            }

            return true;
        }
    }
}
