package net.minecraft.server;

// CraftBukkit start

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;
// CraftBukkit end

public class InventoryEnderChest extends InventorySubcontainer {

    private TileEntityEnderChest a;

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<>();
    public org.bukkit.entity.Player player;
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return this.player;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    public int getMaxStackSize() {
        return maxStack;
    }
    // CraftBukkit end

    public InventoryEnderChest() {
        super("container.enderchest", false, 27);
    }

    public void a(TileEntityEnderChest tileentityenderchest) {
        this.a = tileentityenderchest;
    }

    public void a(NBTTagList nbttaglist) {
        int i;

        for (i = 0; i < this.getSize(); ++i) {
            this.setItem(i,null);
        }

        for (i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j < this.getSize()) {
                this.setItem(j, ItemStack.createStack(nbttagcompound));
            }
        }

    }

    public NBTTagList h() {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (itemstack != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public boolean a(EntityHuman entityhuman) {
        return (this.a == null || this.a.a(entityhuman)) && super.a(entityhuman);
    }

    public void startOpen(EntityHuman entityhuman) {
        if (this.a != null) {
            this.a.b();
        }

        super.startOpen(entityhuman);
    }

    public void closeContainer(EntityHuman entityhuman) {
        if (this.a != null) {
            this.a.d();
        }

        super.closeContainer(entityhuman);
        this.a = null;
    }
}