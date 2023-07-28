package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityFurnace;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.inventory.FurnaceInventory;

public class CraftFurnace extends CraftBlockState implements Furnace {
    private final TileEntityFurnace furnace;

    public CraftFurnace(Block block) {
        super(block);
        this.furnace = (TileEntityFurnace)((CraftWorld)block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    public CraftFurnace(Material material, TileEntityFurnace te) {
        super(material);
        this.furnace = te;
    }

    public FurnaceInventory getInventory() {
        return new CraftInventoryFurnace(this.furnace);
    }

    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result)
            this.furnace.update();
        return result;
    }

    public short getBurnTime() {
        return (short)this.furnace.burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.furnace.burnTime = burnTime;
    }

    public short getCookTime() {
        return (short)this.furnace.cookTime;
    }

    public void setCookTime(short cookTime) {
        this.furnace.cookTime = cookTime;
    }

    public TileEntityFurnace getTileEntity() {
        return this.furnace;
    }
}
