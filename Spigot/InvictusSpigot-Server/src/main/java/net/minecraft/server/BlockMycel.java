package net.minecraft.server;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Random;
// CraftBukkit end

public class BlockMycel extends Block {

    public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

    protected BlockMycel() {
        super(Material.GRASS, MaterialMapColor.z);
        this.j(this.blockStateList.getBlockData().set(BlockMycel.SNOWY, false));
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock();

        return iblockdata.set(BlockMycel.SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.getLightLevel(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()) < 4 && world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().p() > 2) {
            // CraftBukkit start
            // world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, EnumDirtVariant.DIRT));
            org.bukkit.World bworld = world.getWorld();
            BlockState blockState = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()).getState();
            blockState.setType(CraftMagicNumbers.getMaterial(Blocks.DIRT));

            BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
            world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                blockState.update(true);
            }
            // CraftBukkit end
        } else {
            if (world.getLightLevel(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()) >= 9) {
                for (int i = 0; i < Math.min(4, Math.max(20, (int) (4 * 100F / world.growthOdds))); ++i) { // Spigot
                    BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    IBlockData iblockdata1 = world.getType(blockposition1);
                    Block block = world.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()).getBlock();

                    if (iblockdata1.getBlock() == Blocks.DIRT && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT && world.getLightLevel(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()) >= 4 && block.p() <= 2) {
                        // CraftBukkit start
                        // world.setTypeUpdate(blockposition1, this.getBlockData());
                        org.bukkit.World bworld = world.getWorld();
                        BlockState blockState = bworld.getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()).getState();
                        blockState.setType(CraftMagicNumbers.getMaterial(this));

                        BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), blockState);
                        world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            blockState.update(true);
                        }
                        // CraftBukkit end
                    }
                }
            }

        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    public BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[]{BlockMycel.SNOWY});
    }
}
