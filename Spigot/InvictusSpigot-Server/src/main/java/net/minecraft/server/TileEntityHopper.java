package net.minecraft.server;

import net.techcable.tacospigot.HopperHelper;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class TileEntityHopper extends TileEntityContainer implements IHopper, IUpdatePlayerListBox {

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<>();
    private ItemStack[] items = new ItemStack[5];
    private String f;
    // private int g = -1;
    private int g = -1;
    private int maxStack = MAX_STACK;

    private static boolean b(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            for (int j : iworldinventory.getSlotsForFace(enumdirection)) {
                if (iworldinventory.getItem(j) != null) {
                    return false;
                }
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j; ++k) {
                if (iinventory.getItem(k) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    // TacoSpigot start - Split methods, one that pushes and one that pulls
    @Deprecated
    public static boolean a(IHopper ihopper) {
        IInventory iinventory;
        if (ihopper instanceof TileEntityHopper) {
            // everything else comes to us
            iinventory = HopperHelper.getInventory(ihopper.getWorld(), ((TileEntityHopper) ihopper).getPosition().up()); // Only pull from a above, because
        } else {
            iinventory = b(ihopper); // Use old behavior for BB entity searching
        }
        return acceptItem(ihopper, iinventory);
    }

    public static boolean acceptItem(IHopper ihopper, IInventory iinventory) {
        // TacoSpigot end
        if (iinventory != null) {
            EnumDirection enumdirection = EnumDirection.DOWN;

            if (b(iinventory, enumdirection)) {
                return false;
            }

            if (iinventory instanceof IWorldInventory) {
                for (int j : ((IWorldInventory) iinventory).getSlotsForFace(enumdirection)) {
                    if (a(ihopper, iinventory, j, enumdirection)) {
                        return true;
                    }
                }
            } else {
                int j = iinventory.getSize();

                for (int k = 0; k < j; ++k) {
                    if (a(ihopper, iinventory, k, enumdirection)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean a(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
        ItemStack itemstack = iinventory.getItem(i);

        if (itemstack != null && b(iinventory, itemstack, i, enumdirection)) {
            ItemStack itemstack1 = itemstack.cloneItemStack();

            // CraftBukkit start - Call event on collection of items from inventories into
            // the hopper
            CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.splitStack(i, ihopper.getWorld().spigotConfig.hopperAmount)); // Spigot
            // TacoSpigot start - add an option to turn of InventoryMoveItemEvent
            final org.bukkit.inventory.ItemStack stack;
            if (HopperHelper.isFireInventoryMoveItemEvent(ihopper)) {
                // TacoSpigot end

                Inventory sourceInventory;
                // Have to special case large chests as they work oddly
                if (iinventory instanceof InventoryLargeChest) {
                    sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
                } else {
                    sourceInventory = iinventory.getOwner().getInventory();
                }

                InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(),
                        ihopper.getOwner().getInventory(), false);

                ihopper.getWorld().getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    iinventory.setItem(i, itemstack1);

                    if (ihopper instanceof TileEntityHopper) {
                        ((TileEntityHopper) ihopper).d(ihopper.getWorld().spigotConfig.hopperTransfer); // Spigot
                    } else if (ihopper instanceof EntityMinecartHopper) {
                        ((EntityMinecartHopper) ihopper).m(ihopper.getWorld().spigotConfig.hopperTransfer / 2); // Spigot
                    }
                    return false;
                }
                // TacoSpigot start
                stack = event.getItem();
                // handle cases where the event is not fired
            } else {
                stack = oitemstack;
            }
            int origCount = stack.getAmount(); // Spigot
            ItemStack itemstack2 = addItem(ihopper, CraftItemStack.asNMSCopy(stack), null);
            // TacoSpigot end

            if (itemstack2 == null || itemstack2.count == 0) {
                if (stack.equals(oitemstack)) {
                    iinventory.update();
                } else {
                    iinventory.setItem(i, itemstack1);
                }
                // CraftBukkit end
                return true;
            }
            itemstack1.count -= origCount - itemstack2.count; // Spigot

            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean a(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            // CraftBukkit start
            InventoryPickupItemEvent event = new InventoryPickupItemEvent(iinventory.getOwner().getInventory(),
                    (org.bukkit.entity.Item) entityitem.getBukkitEntity());
            entityitem.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end

            ItemStack itemstack1 = addItem(iinventory, entityitem.getItemStack().cloneItemStack(), null);

            if (itemstack1 != null && itemstack1.count != 0) {
                entityitem.setItemStack(itemstack1);
            } else {
                flag = true;
                entityitem.die();
            }

            return flag;
        }
    }

    public static ItemStack addItem(IInventory iinventory, ItemStack itemstack, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory && enumdirection != null) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length && itemstack != null && itemstack.count > 0; ++i) {
                itemstack = c(iinventory, itemstack, aint[i], enumdirection);
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j && itemstack != null && itemstack.count > 0; ++k) {
                itemstack = c(iinventory, itemstack, k, enumdirection);
            }
        }

        if (itemstack != null && itemstack.count == 0) {
            itemstack = null;
        }

        return itemstack;
    }

    private static boolean a(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return iinventory.b(i, itemstack) && (!(iinventory instanceof IWorldInventory)
                || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection));
    }
    // CraftBukkit end

    private static boolean b(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !(iinventory instanceof IWorldInventory)
                || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
    }

    private static ItemStack c(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        ItemStack itemstack1 = iinventory.getItem(i);

        if (a(iinventory, itemstack, i, enumdirection)) {
            boolean flag = false;

            if (itemstack1 == null) {
                iinventory.setItem(i, itemstack);

                itemstack = null;
                flag = true;
            } else if (a(itemstack1, itemstack)) {
                int k = Math.min(itemstack.count, itemstack.getMaxStackSize() - itemstack1.count);

                itemstack.count -= k;
                itemstack1.count += k;
                flag = k > 0;
            }

            if (flag) {
                if (iinventory instanceof TileEntityHopper) {
                    TileEntityHopper tileentityhopper = (TileEntityHopper) iinventory;

                    if (tileentityhopper.o()) {
                        tileentityhopper.d(tileentityhopper.world.spigotConfig.hopperTransfer); // Spigot
                    }

                    iinventory.update();
                }

                iinventory.update();
            }
        }

        return itemstack;
    }

    public static IInventory b(IHopper ihopper) {
        return b(ihopper.getWorld(), ihopper.A(), ihopper.B() + 1.0D, ihopper.C());
    }

    public static List<EntityItem> a(World world, double d0, double d1, double d2) {
        return world.a(EntityItem.class, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.a);
    }

    public static IInventory b(World world, double d0, double d1, double d2) {
        Object object = null;
        int i = MathHelper.floor(d0), j = MathHelper.floor(d1), k = MathHelper.floor(d2);
        if(!world.isValidLocation(i, j, k))
            return null;
        long hash = LongHash.toLong(i >> 4, k >> 4);
        Chunk chunk = world.getChunkIfLoaded(hash);
        if (chunk == null || ((ChunkProviderServer) world.chunkProvider).unloadQueue.contains(hash))
            return null;
        Block block = world.getType(chunk, i, j, k, true).getBlock();

        BlockPosition blockposition = new BlockPosition(i, j, k);
        if (block.isTileEntity()) {
            TileEntity tileentity = ((WorldServer)world).getTileEntity(chunk, blockposition, block);

            if (tileentity instanceof IInventory) {
                object = tileentity;
                if (object instanceof TileEntityChest && block instanceof BlockChest) {
                    object = ((BlockChest) block).f(world, blockposition);
                }
            }
        }

        if (object == null && !CraftMagicNumbers.getMaterial(block).isOccluding() && chunk.getItemCount(blockposition) > 0) {
            List<Entity> list = world.a((Entity) null,
                    new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D),
                    IEntitySelector.c);

            if (list.size() > 0) {
                object = list.get(world.random.nextInt(list.size()));
            }
        }

        return (IInventory) object;
    }

    private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() == itemstack1.getItem() && (itemstack.getData() == itemstack1.getData() && (itemstack.count <= itemstack.getMaxStackSize() && ItemStack.equals(itemstack, itemstack1)));
    }

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

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        this.items = new ItemStack[this.getSize()];
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.f = nbttagcompound.getString("CustomName");
        }

        this.g = nbttagcompound.getInt("TransferCooldown");

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.items.length) {
                this.items[b0] = ItemStack.createStack(nbttagcompound1);
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Slot", (byte) i);
                this.items[i].save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Items", nbttaglist);
        nbttagcompound.setInt("TransferCooldown", this.g);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.f);
        }

    }

    public int getSize() {
        return this.items.length;
    }

    public ItemStack getItem(int i) {
        return this.items[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack = items[i];
            if (itemstack.count <= j) {
                this.items[i] = null;
            } else {
                if (itemstack.count == 0) {
                    this.items[i] = null;
                }
                itemstack = itemstack.cloneAndSubtract(j);
            }
            return itemstack;
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.items[i] != null) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }
    // TacoSpigot end

    public void setItem(int i, ItemStack itemstack) {
        this.items[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.f : "container.hopper";
    }

    public boolean hasCustomName() {
        return this.f != null && this.f.length() > 0;
    }

    public void a(String s) {
        this.f = s;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) == this && entityhuman.e(this.position.getX() + 0.5D, this.position.getY() + 0.5D,
                this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {
    }

    public void closeContainer(EntityHuman entityhuman) {
    }

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void c() {
        if (this.world != null) {
            --this.g;
            if (!this.n()) {
                this.d(0);
                this.m();
            }

        }
    }

    public boolean m() {
        if (this.world != null) {
            if (!this.n() && BlockHopper.f(this.u())) {
                boolean flag = false;

                if (!this.p()) {
                    flag = this.r();
                }

                if (!this.q()) {
                    flag = a(this) || flag;
                }

                if (flag) {
                    this.d(world.spigotConfig.hopperTransfer); // Spigot
                    this.update();
                    return true;
                }
            }

        }
        return false;
    }

    private boolean p() {
        for (ItemStack itemstack : items) {
            if (itemstack != null) {
                return false;
            }
        }

        return true;
    }

    public boolean canAcceptItems() {
        return !this.n() && !this.q() && BlockHopper.f(this.u());
    }

    private boolean q() {
        for (ItemStack itemstack : items) {
            if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean r() {
        // TacoSpigot start - Don't use inefficient H() which does another bounding box
        // search

        EnumDirection enumdirection = BlockHopper.b(this.u());
        IInventory iinventory = HopperHelper.getInventory(getWorld(), getPosition().shift(enumdirection));
        // TacoSpigot end

        if (iinventory != null) {
            EnumDirection enumdirection1 = enumdirection.opposite();

            if (!this.a(iinventory, enumdirection1)) {
                for (int i = 0; i < getSize(); ++i) {
                    ItemStack item = getItem(i);
                    if (item != null) {
                        ItemStack itemstack = item.cloneItemStack();

                        // CraftBukkit start - Call event when pushing items into other inventories
                        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(this.splitStack(i, world.spigotConfig.hopperAmount)); // Spigot
                        // TacoSpigot start - add an option to turn of InventoryMoveItemEvent
                        final org.bukkit.inventory.ItemStack stack;
                        if (HopperHelper.isFireInventoryMoveItemEvent(this)) {
                            // TacoSpigot end

                            Inventory destinationInventory;
                            // Have to special case large chests as they work oddly
                            if (iinventory instanceof InventoryLargeChest) {
                                destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest(
                                        (InventoryLargeChest) iinventory);
                            } else {
                                destinationInventory = iinventory.getOwner().getInventory();
                            }

                            InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.getOwner().getInventory(),
                                    oitemstack.clone(), destinationInventory, true);
                            this.getWorld().getServer().getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                this.setItem(i, itemstack);
                                this.d(world.spigotConfig.hopperTransfer); // Spigot
                                return false;
                            }
                            // TacoSpigot start
                            stack = event.getItem();
                            // handle cases where the event is not fired
                        } else {
                            stack = oitemstack;
                        }
                        int origCount = stack.getAmount(); // Spigot
                        ItemStack itemstack1 = addItem(iinventory, CraftItemStack.asNMSCopy(stack), enumdirection1);
                        // TacoSpigot end

                        if (itemstack1 == null || itemstack1.count == 0) {
                            if (stack.equals(oitemstack)) { // TacoSpigot - event.getItem() -> stack
                                iinventory.update();
                            } else {
                                this.setItem(i, itemstack);
                            }
                            // CraftBukkit end
                            return true;
                        }
                        itemstack.count -= origCount - itemstack1.count; // Spigot
                        this.setItem(i, itemstack);
                    }
                }

            }
        }
        return false;
    }

    private boolean a(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;

            for (int j : iworldinventory.getSlotsForFace(enumdirection)) {
                ItemStack itemstack = iworldinventory.getItem(j);

                if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j; ++k) {
                ItemStack itemstack1 = iinventory.getItem(k);

                if (itemstack1 == null || itemstack1.count != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    public double A() {
        return this.position.getX() + 0.5D;
    }

    public double B() {
        return this.position.getY() + 0.5D;
    }

    public double C() {
        return this.position.getZ() + 0.5D;
    }

    public void d(int i) {
        this.g = i;
    }

    public boolean n() {
        return this.g > 0;
    }

    public boolean o() {
        return this.g <= 1;
    }

    public String getContainerName() {
        return "minecraft:hopper";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerHopper(playerinventory, this, entityhuman);
    }

    public int getProperty(int i) {
        return 0;
    }

    public void b(int i, int j) {
    }

    public int g() {
        return 0;
    }

    public void l() {
        Arrays.fill(this.items, null);

    }
}
