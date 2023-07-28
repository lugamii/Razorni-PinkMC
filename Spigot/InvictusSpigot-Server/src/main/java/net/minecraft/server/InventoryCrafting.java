package net.minecraft.server;

// CraftBukkit start

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;
import java.util.List;
// CraftBukkit end

public class InventoryCrafting implements IInventory {

    private final ItemStack[] items;
    private final int b;
    private final int c;
    private final Container d;

    // CraftBukkit start - add fields
    public List<HumanEntity> transaction = new java.util.ArrayList<>();
    public IRecipe currentRecipe;
    public IInventory resultInventory;
    private EntityHuman owner;

    public ItemStack[] getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public InventoryType getInvType() {
        return items.length == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return (owner == null) ? null : owner.getBukkitEntity();
    }

    public void setMaxStackSize(int size) {
        resultInventory.setMaxStackSize(size);
    }

    public InventoryCrafting(Container container, int i, int j, EntityHuman player) {
        this(container, i, j);
        this.owner = player;
    }
    // CraftBukkit end

    public InventoryCrafting(Container container, int i, int j) {
        int k = i * j;

        this.items = new ItemStack[k];
        this.d = container;
        this.b = i;
        this.c = j;
    }

    public int getSize() {
        return this.items.length;
    }

    public ItemStack getItem(int i) {
        return i >= this.getSize() ? null : this.items[i];
    }

    public ItemStack c(int i, int j) {
        return i >= 0 && i < this.b && j >= 0 && j <= this.c ? this.getItem(i + j * this.b) : null;
    }

    public String getName() {
        return "container.crafting";
    }

    public boolean hasCustomName() {
        return false;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName()));
    }

    public ItemStack splitWithoutUpdate(int i) {
        ItemStack itemstack = items[i];
        if (itemstack != null) {
            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack itemstack = items[i];
        if (itemstack != null) {
            if (itemstack.count <= j) {
                this.items[i] = null;
            } else {
                if (itemstack.count == 0) {
                    this.items[i] = null;
                }
                itemstack = itemstack.cloneAndSubtract(j);
            }
            this.d.a(this);
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.items[i] = itemstack;
        this.d.a(this);
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void update() {}

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void b(int i, int j) {}

    public int g() {
        return 0;
    }

    public void l() {
        Arrays.fill(this.items, null);

    }

    public int h() {
        return this.c;
    }

    public int i() {
        return this.b;
    }
}
