package eu.vortexdev.invictusspigot.util;

import net.minecraft.server.IBlockData;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.Vector;

public class BoundingBox {
    private final Vector max, min;

    public BoundingBox(Block block) {
        IBlockData blockData = ((CraftWorld) block.getWorld()).getHandle().getType(block.getX(), block.getY(), block.getZ());
        net.minecraft.server.Block blockNative = blockData.getBlock();
        min = new Vector(block.getX() + blockNative.B(), block.getY() + blockNative.D(), block.getZ() + blockNative.F());
        max = new Vector(block.getX() + blockNative.C(), block.getY() + blockNative.E(), block.getZ() + blockNative.G());
    }

    public Vector getMax() {
        return max;
    }

    public Vector getMin() {
        return min;
    }
}
