package net.minecraft.server;

import java.util.Random;

public class WorldGenHellLava extends WorldGenerator {

    private final Block a;
    private final boolean b;

    public WorldGenHellLava(Block block, boolean flag) {
        this.a = block;
        this.b = flag;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        if (world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock() != Blocks.NETHERRACK) {
            return false;
        } else if (world.getType(blockposition).getBlock().getMaterial() != Material.AIR && world.getType(blockposition).getBlock() != Blocks.NETHERRACK) {
            return false;
        } else {
            int i = 0;

            if (world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ()).getBlock() == Blocks.NETHERRACK) {
                ++i;
            }

            if (world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ()).getBlock() == Blocks.NETHERRACK) {
                ++i;
            }

            if (world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1).getBlock() == Blocks.NETHERRACK) {
                ++i;
            }

            if (world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1).getBlock() == Blocks.NETHERRACK) {
                ++i;
            }

            if (world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() == Blocks.NETHERRACK) {
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

            if (world.isEmpty(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())) {
                ++j;
            }

            if (!this.b && i == 4 && j == 1 || i == 5) {
                world.setTypeAndData(blockposition, this.a.getBlockData(), 2);
                world.a(this.a, blockposition, random);
            }

            return true;
        }
    }
}
