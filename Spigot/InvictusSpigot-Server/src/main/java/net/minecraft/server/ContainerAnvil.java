package net.minecraft.server;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.PrepareItemAnvilRepairEvent;

import java.util.Map;

public class ContainerAnvil extends Container {

    private final IInventory g = new InventoryCraftResult();
    private final IInventory h = new InventorySubcontainer("Repair", true, 2) {
        public void update() {
            super.update();
            ContainerAnvil.this.a(this);
        }
    };
    private final World i;
    private final BlockPosition j;
    public int a;
    private int k;
    private String l;
    private final EntityHuman m;
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private final PlayerInventory player;
    // CraftBukkit end

    public ContainerAnvil(PlayerInventory playerinventory, final World world, final BlockPosition blockposition, EntityHuman entityhuman) {
        this.player = playerinventory; // CraftBukkit
        this.j = blockposition;
        this.i = world;
        this.m = entityhuman;
        this.a(new Slot(this.h, 0, 27, 47));
        this.a(new Slot(this.h, 1, 76, 47));
        this.a(new Slot(this.g, 2, 134, 47) {
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            public boolean isAllowed(EntityHuman entityhuman) {
                return (entityhuman.abilities.canInstantlyBuild || entityhuman.expLevel >= ContainerAnvil.this.a) && ContainerAnvil.this.a > 0 && this.hasItem();
            }

            public void a(EntityHuman entityhuman, ItemStack itemstack) {

                if (!entityhuman.abilities.canInstantlyBuild) {
                    entityhuman.levelDown(-ContainerAnvil.this.a);
                }

                ContainerAnvil.this.h.setItem(0, null);
                if (ContainerAnvil.this.k > 0) {
                    ItemStack itemstack1 = ContainerAnvil.this.h.getItem(1);

                    if (itemstack1 != null && itemstack1.count > ContainerAnvil.this.k) {
                        itemstack1.count -= ContainerAnvil.this.k;
                        ContainerAnvil.this.h.setItem(1, itemstack1);
                    } else {
                        ContainerAnvil.this.h.setItem(1, null);
                    }
                } else {
                    ContainerAnvil.this.h.setItem(1, null);
                }

                ContainerAnvil.this.a = 0;
                IBlockData iblockdata = world.getType(blockposition);

                if (!entityhuman.abilities.canInstantlyBuild && iblockdata.getBlock() == Blocks.ANVIL && entityhuman.bc().nextFloat() < 0.12F) {
                    int i = iblockdata.get(BlockAnvil.DAMAGE);

                    ++i;
                    if (i > 2) {
                        world.setAir(blockposition);
                        world.triggerEffect(1020, blockposition, 0);
                    } else {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockAnvil.DAMAGE, i), 2);
                        world.triggerEffect(1021, blockposition, 0);
                    }
                } else {
                    world.triggerEffect(1021, blockposition, 0);
                }

            }
        });

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

    }

    public void a(IInventory iinventory) {
        super.a(iinventory);
        if (iinventory == this.h) {
            this.e();
        }

    }

    public void e() {
        ItemStack item = this.h.getItem(0);
        this.a = 1;
        int i = 0;
        byte b0 = 0;
        byte b1 = 0;
        if (item == null) {
            this.g.setItem(0, null);
            this.a = 0;
        } else {
            ItemStack result = item.cloneItemStack();
            ItemStack second = this.h.getItem(1);
            Map<Integer, Integer> map = EnchantmentManager.a(result);
            int j = b0 + item.getRepairCost() + ((second == null) ? 0 : second.getRepairCost());
            this.k = 0;
            if (second != null) {
                boolean flag7 = (second.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.h(second).size() > 0);
                if (result.e() && result.getItem().a(item, second)) {
                    int k = Math.min(result.h(), result.j() / 4);
                    if (k <= 0) {
                        this.g.setItem(0, null);
                        this.a = 0;
                        return;
                    }
                    int l;
                    for (l = 0; k > 0 && l < second.count; l++) {
                        int i1 = result.h() - k;
                        result.setData(i1);
                        i++;
                        k = Math.min(result.h(), result.j() / 4);
                    }
                    this.k = l;
                } else {
                    if (!flag7 && (result.getItem() != second.getItem() || !result.e())) {
                        this.g.setItem(0, null);
                        this.a = 0;
                        return;
                    }
                    if (result.e() && !flag7) {
                        int k = item.j() - item.h();
                        int l = second.j() - second.h();
                        int i1 = l + result.j() * 12 / 100;
                        int k1 = k + i1;
                        int j1 = result.j() - k1;
                        if (j1 < 0)
                            j1 = 0;
                        if (j1 < result.getData()) {
                            result.setData(j1);
                            i += 2;
                        }
                    }
                    Map<Integer, Integer> map1 = EnchantmentManager.a(second);
                    for (Integer i1 : map1.keySet()) {
                        Enchantment enchantment = Enchantment.getById(i1);
                        if (enchantment != null) {
                            int i2, j1 = map.getOrDefault(i1, 0);
                            int l1 = map1.get(i1);
                            if (j1 == l1) {
                                i2 = ++l1;
                            } else {
                                i2 = Math.max(l1, j1);
                            }
                            l1 = i2;
                            boolean flag8 = enchantment.canEnchant(item);
                            if (this.m.abilities.canInstantlyBuild || item.getItem() == Items.ENCHANTED_BOOK)
                                flag8 = true;
                            for (int j2 : map.keySet()) {
                                if (j2 != i1 && !enchantment.a(Enchantment.getById(j2))) {
                                    flag8 = false;
                                    i++;
                                }
                            }
                            if (flag8) {
                                if (l1 > enchantment.getMaxLevel())
                                    l1 = enchantment.getMaxLevel();
                                map.put(i1, l1);
                                int k2 = 0;
                                switch (enchantment.getRandomWeight()) {
                                    case 1:
                                        k2 = 8;
                                        break;
                                    case 2:
                                        k2 = 4;
                                        break;
                                    case 5:
                                        k2 = 2;
                                        break;
                                    case 10:
                                        k2 = 1;
                                        break;
                                }
                                if (flag7)
                                    k2 = Math.max(1, k2 / 2);
                                i += k2 * l1;
                            }
                        }
                    }
                }
            }
            if (StringUtils.isBlank(this.l)) {
                if (item.hasName()) {
                    b1 = 1;
                    i += b1;
                    result.r();
                }
            } else if (!this.l.equals(item.getName())) {
                b1 = 1;
                i += b1;
                result.c(this.l);
            }
            this.a = j + i;
            if (i <= 0)
                result = null;
            if (b1 == i && b1 > 0 && this.a >= 40)
                this.a = 39;
            if (this.a >= 40 && !this.m.abilities.canInstantlyBuild)
                result = null;
            if (result != null) {
                int k = result.getRepairCost();
                if (second != null && k < second.getRepairCost())
                    k = second.getRepairCost();
                k = k * 2 + 1;
                result.setRepairCost(k);
                EnchantmentManager.a(map, result);
                PrepareItemAnvilRepairEvent event = new PrepareItemAnvilRepairEvent(getBukkitView(), this.m.getBukkitEntity(), this.m.world.getWorld().getBlockAt(this.j.getX(), this.j.getY(), this.j.getZ()), this.a, CraftItemStack.asBukkitCopy(item), CraftItemStack.asBukkitCopy(second), CraftItemStack.asBukkitCopy(result));
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled() || event.getResult().getType() == org.bukkit.Material.AIR)
                    return;
                this.a = event.getRepairCost();
                result = CraftItemStack.asNMSCopy(event.getResult());
            }
            this.g.setItem(0, result);
            b();
        }
    }

    public void addSlotListener(ICrafting icrafting) {
        super.addSlotListener(icrafting);
        icrafting.setContainerData(this, 0, this.a);
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
 
            for (int i = 0; i < this.h.getSize(); ++i) {
                ItemStack itemstack = this.h.splitWithoutUpdate(i);

                if (itemstack != null) {
                    entityhuman.drop(itemstack, false);
                }
            }

    }

    public boolean a(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return this.i.getType(this.j).getBlock() == Blocks.ANVIL && entityhuman.e(this.j.getX() + 0.5D, this.j.getY() + 0.5D, this.j.getZ() + 0.5D) <= 64.0D;
    }

    public ItemStack b(EntityHuman entityhuman, int i) {
        ItemStack itemstack = null;
        Slot slot = this.c.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return null;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i < 39 && !this.a(itemstack1, 0, 2, false)) {
                    return null;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return null;
            }

            if (itemstack1.count == 0) {
                slot.set(null);
            } else {
                slot.f();
            }

            if (itemstack1.count == itemstack.count) {
                return null;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    public void a(String s) {
        this.l = s;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemstack = this.getSlot(2).getItem();

            if (StringUtils.isBlank(s)) {
                itemstack.r();
            } else {
                itemstack.c(this.l);
            }
        }

        this.e();
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        org.bukkit.craftbukkit.inventory.CraftInventory inventory = new org.bukkit.craftbukkit.inventory.CraftInventoryAnvil(this.h, this.g);
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
