package net.techcable.tacospigot;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class HopperHelper {

    public static TileEntityHopper getHopper(World world, BlockPosition pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        IBlockData iblockdata = null;
        if (world.captureTreeGeneration) {
            for (org.bukkit.block.BlockState previous : world.capturedBlockStates) {
                if (previous.getX() == x && previous.getY() == y && previous.getZ() == z) {
                    iblockdata = CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
                }
            }
        }

        if(iblockdata == null && !world.isValidLocation(x, y, z))
            return null;

        Chunk chunk = world.getChunkAtWorldCoords(x, z);
        Block block = chunk.getBlockData(x, y, z).getBlock();
        if (block != Blocks.HOPPER) return null;

        TileEntity tileEntity = ((WorldServer)world).getTileEntity(chunk, pos, block);
        if (tileEntity instanceof TileEntityHopper) {
            return (TileEntityHopper) tileEntity;
        }
        return null;
    }

    public static IInventory getInventory(World world, BlockPosition position) {
        if(!world.isValidLocation(position.getX(), position.getY(), position.getZ()))
            return null;
        Chunk chunk = world.getChunkIfLoaded(position.getX() >> 4, position.getZ() >> 4);
        if(chunk == null)
            return null;
        Block block = world.getType(chunk, position.getX(), position.getY(), position.getZ(), true).getBlock();
        if (block instanceof BlockChest) {
            return ((BlockChest) block).f(world, position);
        } else if (block.isTileEntity()) {
            TileEntity tile = world.getTileEntity(position);
            if (tile instanceof IInventory) return (IInventory) tile;
        }
        return null;
    }

    public static boolean isFireInventoryMoveItemEvent(IHopper hopper) {
        return InvictusConfig.hopperInventoryMoveItemEvent && InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length > 0;
    }
}
