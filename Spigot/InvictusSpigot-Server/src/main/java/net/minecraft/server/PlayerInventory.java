package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventory implements IInventory {
    public ItemStack[] items = new ItemStack[36];

    public ItemStack[] armor = new ItemStack[4];

    public int itemInHandIndex;

    public EntityHuman player;

    private ItemStack f;

    public boolean e;

    public List<HumanEntity> transaction = new ArrayList<>();

    private int maxStack = 64;

    public ItemStack[] getContents() {
        return this.items;
    }

    public ItemStack[] getArmorContents() {
        return this.armor;
    }

    public void onOpen(CraftHumanEntity who) {
        this.transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        this.transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return this.transaction;
    }

    public InventoryHolder getOwner() {
        return this.player.getBukkitEntity();
    }

    public void setMaxStackSize(int size) {
        this.maxStack = size;
    }

    public PlayerInventory(EntityHuman entityhuman) {
        this.player = entityhuman;
    }

    public ItemStack getItemInHand() {
        return (this.itemInHandIndex < 9 && this.itemInHandIndex >= 0) ? this.items[this.itemInHandIndex] : null;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private int c(Item item) {
        for (int i = 0; i < this.items.length; i++) {
            ItemStack itemstack = items[i];
            if (itemstack != null && itemstack.getItem() == item)
                return i;
        }
        return -1;
    }

    private int firstPartial(ItemStack itemstack) {
        for (int i = 0; i < this.items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getItem() == itemstack.getItem() && item.isStackable() && item.count < item.getMaxStackSize() && item.count < getMaxStackSize() && (!item.usesData() || item.getData() == itemstack.getData()) && ItemStack.equals(item, itemstack))
                return i;
        }
        return -1;
    }

    public int canHold(ItemStack itemstack) {
        int remains = itemstack.count;
        for (ItemStack item : this.items) {
            if (item == null)
                return itemstack.count;
            if (item.getItem() == itemstack.getItem() && item.isStackable() && item.count < item.getMaxStackSize() && item.count < getMaxStackSize() && (!item.usesData() || item.getData() == itemstack.getData()) && ItemStack.equals(item, itemstack))
                remains -= (Math.min(item.getMaxStackSize(), getMaxStackSize())) - item.count;
            if (remains <= 0)
                return itemstack.count;
        }
        return itemstack.count - remains;
    }

    public int getFirstEmptySlotIndex() {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null)
                return i;
        }
        return -1;
    }

    public int a(Item item, int i, int j, NBTTagCompound nbttagcompound) {
        int k = 0;
        int l;
        for (l = 0; l < this.items.length; l++) {
            ItemStack itemstack = this.items[l];
            if (itemstack != null && (item == null || itemstack.getItem() == item) && (i <= -1 || itemstack.getData() == i) && (nbttagcompound == null || GameProfileSerializer.a(nbttagcompound, itemstack.getTag(), true))) {
                int i1 = (j <= 0) ? itemstack.count : Math.min(j - k, itemstack.count);
                k += i1;
                if (j != 0) {
                    (this.items[l]).count -= i1;
                    if ((this.items[l]).count == 0)
                        this.items[l] = null;
                    if (j > 0 && k >= j)
                        return k;
                }
            }
        }
        for (l = 0; l < this.armor.length; l++) {
            ItemStack itemstack = this.armor[l];
            if (itemstack != null && (item == null || itemstack.getItem() == item) && (i <= -1 || itemstack.getData() == i) && (nbttagcompound == null || GameProfileSerializer.a(nbttagcompound, itemstack.getTag(), false))) {
                int i1 = (j <= 0) ? itemstack.count : Math.min(j - k, itemstack.count);
                k += i1;
                if (j != 0) {
                    (this.armor[l]).count -= i1;
                    if ((this.armor[l]).count == 0)
                        this.player.setEquipment(l, null);
                    if (j > 0 && k >= j)
                        return k;
                }
            }
        }
        if (this.f != null) {
            if (item != null && this.f.getItem() != item)
                return k;
            if (i > -1 && this.f.getData() != i)
                return k;
            if (nbttagcompound != null && !GameProfileSerializer.a(nbttagcompound, this.f.getTag(), false))
                return k;
            l = (j <= 0) ? this.f.count : Math.min(j - k, this.f.count);
            k += l;
            if (j != 0) {
                this.f.count -= l;
                if (this.f.count == 0)
                    this.f = null;
                if (j > 0 && k >= j)
                    return k;
            }
        }
        return k;
    }

    private int e(ItemStack itemstack) {
        Item item = itemstack.getItem();
        int i = itemstack.count;
        int j = firstPartial(itemstack);
        if (j < 0)
            j = getFirstEmptySlotIndex();
        if (j < 0)
            return i;
        ItemStack itemstack1 = items[j];
        if (itemstack1 == null) {
            itemstack1 = new ItemStack(item, 0, itemstack.getData());
            this.items[j] = itemstack1;
            if (itemstack.hasTag())
                itemstack1.setTag((NBTTagCompound)itemstack.getTag().clone());
        }
        int k = i;
        if (i > itemstack1.getMaxStackSize() - itemstack1.count)
            k = itemstack1.getMaxStackSize() - itemstack1.count;
        if (k > getMaxStackSize() - itemstack1.count)
            k = getMaxStackSize() - itemstack1.count;
        if (k == 0)
            return i;
        i -= k;
        itemstack1.count += k;
        itemstack1.c = 5;
        return i;
    }

    public void k() {
        for (int i = 0; i < this.items.length; i++) {
            ItemStack item = items[i];
            if (item != null)
                item.a(this.player.world, this.player, i, (this.itemInHandIndex == i));
        }
    }

    public boolean a(Item item) {
        int i = c(item);
        if (i < 0)
            return false;
        if (--(this.items[i]).count <= 0)
            this.items[i] = null;
        return true;
    }

    public boolean b(Item item) {
        int i = c(item);
        return (i >= 0);
    }

    public boolean pickup(final ItemStack itemstack) {
        if (itemstack != null && itemstack.count != 0 && itemstack.getItem() != null)
            try {
                int i;
                if (itemstack.g()) {
                    i = getFirstEmptySlotIndex();
                    if (i >= 0) {
                        ItemStack item = ItemStack.b(itemstack);
                        this.items[i] = item;
                        item.c = 5;
                        itemstack.count = 0;
                        return true;
                    }
                    if (this.player.abilities.canInstantlyBuild) {
                        itemstack.count = 0;
                        return true;
                    }
                    return false;
                }
                do {
                    i = itemstack.count;
                    itemstack.count = e(itemstack);
                } while (itemstack.count > 0 && itemstack.count < i);
                if (itemstack.count == i && this.player.abilities.canInstantlyBuild) {
                    itemstack.count = 0;
                    return true;
                }
                return (itemstack.count < i);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");
                crashreportsystemdetails.a("Item ID", Item.getId(itemstack.getItem()));
                crashreportsystemdetails.a("Item data", itemstack.getData());
                crashreportsystemdetails.a("Item name", itemstack::getName);
                throw new ReportedException(crashreport);
            }
        return false;
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack[] aitemstack = this.items;
        boolean settingArmour = (i >= this.items.length);
        if (settingArmour) {
            aitemstack = this.armor;
            i -= this.items.length;
        }
        ItemStack itemStack = aitemstack[i];
        if (itemStack != null) {
            if (itemStack.count <= j) {
                if (settingArmour) {
                    this.player.setEquipment(i, null);
                } else {
                    aitemstack[i] = null;
                }
                return itemStack;
            }
            ItemStack itemstack = itemStack.cloneAndSubtract(j);
            if (itemStack.count == 0) {
                aitemstack[i] = null;
                if (settingArmour) {
                    this.player.setEquipment(i, null);
                }
            }
            return itemstack;
        }
        return null;
    }

    public ItemStack splitWithoutUpdate(int i) {
        ItemStack[] aitemstack = this.items;
        boolean settingArmour = (i >= this.items.length);
        if (settingArmour) {
            aitemstack = this.armor;
            i -= this.items.length;
        }
        ItemStack itemstack = aitemstack[i];
        if (itemstack != null) {
            if (settingArmour) {
                this.player.setEquipment(i, null);
            } else {
                aitemstack[i] = null;
            }
            return itemstack;
        }
        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        ItemStack[] aitemstack = this.items;
        if (i >= aitemstack.length) {
            i -= aitemstack.length;
            this.player.setEquipment(i, itemstack);
        } else {
            aitemstack[i] = itemstack;
        }
    }

    public float a(Block block) {
        float f = 1.0F;
        ItemStack hand = items[itemInHandIndex];
        if (hand != null)
            f *= hand.a(block);
        return f;
    }

    public NBTTagList a(NBTTagList nbttaglist) {
        int i;
        for (i = 0; i < this.items.length; i++) {
            ItemStack item = items[i];
            if (item != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                item.save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }
        for (i = 0; i < this.armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)(i + 100));
                item.save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }
        return nbttaglist;
    }

    public void b(NBTTagList nbttaglist) {
        this.items = new ItemStack[36];
        this.armor = new ItemStack[4];
        for (int i = 0; i < nbttaglist.size(); i++) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            int j = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = ItemStack.createStack(nbttagcompound);
            if (itemstack != null) {
                if (j < this.items.length)
                    this.items[j] = itemstack;
                if (j >= 100 && j < this.armor.length + 100)
                    this.player.setEquipment(j - 100, itemstack);
            }
        }
    }

    public int getSize() {
        return this.items.length + 4;
    }

    public ItemStack getItem(int i) {
        ItemStack[] aitemstack = this.items;
        if (i >= aitemstack.length) {
            i -= aitemstack.length;
            aitemstack = this.armor;
        }
        return aitemstack[i];
    }

    public String getName() {
        return "container.inventory";
    }

    public boolean hasCustomName() {
        return false;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return hasCustomName() ? new ChatComponentText(getName()) : new ChatMessage(getName());
    }

    public int getMaxStackSize() {
        return this.maxStack;
    }

    public boolean b(Block block) {
        if (block.getMaterial().isAlwaysDestroyable())
            return true;
        ItemStack itemstack = getItem(this.itemInHandIndex);
        return (itemstack != null && itemstack.b(block));
    }

    public ItemStack e(int i) {
        return this.armor[i];
    }

    public int m() {
        int i = 0;
        for (ItemStack itemStack : this.armor) {
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                int k = ((ItemArmor) itemStack.getItem()).c;
                i += k;
            }
        }
        return i;
    }

    public void a(float f) {
        f /= InvictusConfig.armorDamage;
        if (f < 1.0F)
            f = 1.0F;
        for (int i = 0; i < this.armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null && item.getItem() instanceof ItemArmor) {
                item.damage((int)f, this.player);
                if (item.count == 0)
                    this.player.setEquipment(i, null);
            }
        }
    }

    public void n() {
        int i;
        for (i = 0; i < this.items.length; i++) {
            ItemStack item = items[i];
            if (item != null) {
                this.player.a(item, true, false);
                this.items[i] = null;
            }
        }
        for (i = 0; i < this.armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null) {
                this.player.a(item, true, false);
                this.player.setEquipment(i, null);
            }
        }
    }

    public void update() {
        this.e = true;
    }

    public void setCarried(ItemStack itemstack) {
        this.f = itemstack;
    }

    public ItemStack getCarried() {
        if (this.f != null && this.f.count == 0)
            setCarried(null);
        return this.f;
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.player.dead && ((entityhuman.h(this.player) <= 64.0D));
    }

    public boolean c(ItemStack itemstack) {
        int i;
        for (i = 0; i < this.armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null && item.doMaterialsMatch(itemstack))
                return true;
        }
        for (i = 0; i < this.items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.doMaterialsMatch(itemstack))
                return true;
        }
        return false;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void b(PlayerInventory playerinventory) {
        int i;
        for (i = 0; i < this.items.length; i++)
            this.items[i] = ItemStack.b(playerinventory.items[i]);
        for (i = 0; i < this.armor.length; i++)
            this.player.setEquipment(i, ItemStack.b(playerinventory.armor[i]));
        this.itemInHandIndex = playerinventory.itemInHandIndex;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void b(int i, int j) {}

    public int g() {
        return 0;
    }

    public void l() {
        int i;
        for (i = 0; i < this.items.length; i++)
            this.items[i] = null;
        for (i = 0; i < this.armor.length; i++)
            this.armor[i] = null;
    }
}
