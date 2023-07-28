package org.bukkit.event.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EquipmentSetEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private HumanEntity humanEntity;
    private int slot;
    private ItemStack previousItem;
    private ItemStack newItem;

    public EquipmentSetEvent(HumanEntity humanEntity, int slot, ItemStack previousItem, ItemStack newItem) {
        this.humanEntity = humanEntity;
        this.slot = slot;
        this.previousItem = previousItem;
        this.newItem = newItem;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HumanEntity getHumanEntity() {
        return this.humanEntity;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getPreviousItem() {
        return this.previousItem;
    }

    public ItemStack getNewItem() {
        return this.newItem;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}