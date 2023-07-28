package org.bukkit.block;

import org.bukkit.inventory.FurnaceInventory;

public interface Furnace extends BlockState, ContainerBlock {
    short getBurnTime();

    void setBurnTime(short paramShort);

    short getCookTime();

    void setCookTime(short paramShort);

    FurnaceInventory getInventory();
}
