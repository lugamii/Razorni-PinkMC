package org.bukkit.event.inventory;

import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PrepareItemAnvilRepairEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final HumanEntity repairer;
    private final Block anvil;
    private final ItemStack first;
    private final ItemStack second;
    private int repairCost;
    private ItemStack result;
    private boolean cancelled;

    public PrepareItemAnvilRepairEvent(InventoryView view, HumanEntity repairer, Block anvil, int repairCost, ItemStack first, ItemStack second, ItemStack result) {
        super(view);
        this.repairer = repairer;
        this.anvil = anvil;
        this.repairCost = repairCost;
        this.first = first;
        this.second = second;
        this.result = result;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HumanEntity getRepairer() {
        return this.repairer;
    }

    public Block getAnvil() {
        return this.anvil;
    }

    public int getRepairCost() {
        return this.repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public ItemStack getFirst() {
        return this.first;
    }

    public ItemStack getSecond() {
        return this.second;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
