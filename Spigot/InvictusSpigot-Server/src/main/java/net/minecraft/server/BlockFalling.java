package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.Random;

public class BlockFalling extends Block {

    public static boolean instaFall;

    public BlockFalling() {
        super(Material.SAND);
        this.a(CreativeModeTab.b);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    public static boolean canFall(World world, int x, int y, int z) {
        Block block = world.getType(x, y, z).getBlock();
        Material material = block.material;

        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

    public static boolean canFall(World world, BlockPosition blockPosition) {
        return canFall(world, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(blockposition, this, this.a(world));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        world.a(blockposition, this, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.f(world, blockposition);
    }

    private void f(World world, BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && (!(this instanceof BlockGravel) || InvictusConfig.gravelGravity) && canFall(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())) {
            if (InvictusConfig.fixSandUnloadingChunk || !instaFall && world.areChunksLoadedBetween(blockposition.a(-32, -32, -32), blockposition.a(32, 32, 32))) {
                // PaperSpigot start - Add FallingBlock source location API
                org.bukkit.Location loc = new org.bukkit.Location(world.getWorld(), blockposition.getX() + 0.5F, blockposition.getY(), blockposition.getZ() + 0.5F);
                EntityFallingBlock entityfallingblock = new EntityFallingBlock(loc, world, blockposition.getX() + 0.5D, blockposition.getY(), blockposition.getZ() + 0.5D, world.getType(blockposition));
                // PaperSpigot end

                a(entityfallingblock);
                world.addEntity(entityfallingblock);
            } else {
                world.setAir(blockposition);

                BlockPosition blockposition1;

                for (blockposition1 = blockposition.down(); canFall(world, blockposition1.getX(), blockposition.getY(), blockposition.getZ()) && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {

                }

                if (blockposition1.getY() > 0) {
                    world.setTypeUpdate(blockposition1.up(), this.getBlockData());
                }
            }

        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {
    }

    public int a(World world) {
        return 2;
    }

    public void a_(World world, BlockPosition blockposition) {
    }
}
