package org.bukkit.event.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;

public class EquipmentSetEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final HumanEntity humanEntity;
    private final int slot;
    private final ItemStack previousItem;
    private final ItemStack newItem;

    @ConstructorProperties({"humanEntity", "slot", "previousItem", "newItem"})
    public EquipmentSetEvent(HumanEntity humanEntity, int slot, ItemStack previousItem, ItemStack newItem) {
        this.humanEntity = humanEntity;
        this.slot = slot;
        this.previousItem = previousItem;
        this.newItem = newItem;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
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

    public HandlerList getHandlers() {
        return handlerList;
    }
}
