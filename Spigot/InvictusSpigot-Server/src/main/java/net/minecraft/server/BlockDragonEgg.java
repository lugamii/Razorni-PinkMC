package net.minecraft.server;

import org.bukkit.event.block.BlockFromToEvent;

import java.util.Random;

public class BlockDragonEgg extends Block {

    public BlockDragonEgg() {
        super(Material.DRAGON_EGG, MaterialMapColor.E);
        this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(blockposition, this, this.a(world));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        world.a(blockposition, this, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.e(world, blockposition);
    }

    private void e(World world, BlockPosition blockposition) {
        final int x = MathHelper.floor(blockposition.getX()), y = MathHelper.floor(blockposition.getY()), z = MathHelper.floor(blockposition.getZ());
        if (y >= 0 && BlockFalling.canFall(world, x, y-1,z)) {

            if (!BlockFalling.instaFall && world.areChunksLoadedBetween(x - 32, y - 32, z - 32, x + 32, y + 32, z +32)) {
                // PaperSpigot start - Add FallingBlock source location API
                world.addEntity(new EntityFallingBlock(new org.bukkit.Location(world.getWorld(), x + 0.5, y, x + 0.5), world,  x + 0.5, y, z + 0.5, this.getBlockData()));
                // PaperSpigot end
            } else {
                world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3, false, true);
                int y1;
                for (y1 = y; BlockFalling.canFall(world, x,y1,z) && y1 > 0; y1--) {
                }

                if (y1 > 0) {
                    world.setTypeAndData(new BlockPosition(x,y1,z), this.getBlockData(), 2, false, true);
                }
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        this.f(world, blockposition);
        return true;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.f(world, blockposition);
    }

    private void f(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == this) {
            for (int i = 0; i < 1000; ++i) {
                BlockPosition blockposition1 = blockposition.a(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));

                if (world.getType(blockposition1).getBlock().material == Material.AIR) {
                    // CraftBukkit start
                    org.bukkit.block.Block from = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                    org.bukkit.block.Block to = world.getWorld().getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
                    BlockFromToEvent event = new BlockFromToEvent(from, to);
                    org.bukkit.Bukkit.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return;
                    }

                    blockposition1 = new BlockPosition(event.getToBlock().getX(), event.getToBlock().getY(), event.getToBlock().getZ());
                    // CraftBukkit end
                    world.setTypeAndData(blockposition1, iblockdata, 2, false, true);
                    world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3, false, true);

                    return;
                }
            }

        }
    }

    public int a(World world) {
        return 5;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }
}
