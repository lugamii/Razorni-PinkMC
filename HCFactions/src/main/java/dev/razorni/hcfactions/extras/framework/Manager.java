package dev.razorni.hcfactions.extras.framework;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.extra.Configs;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Manager extends Configs {
    protected HCF instance;

    public Manager(HCF plugin) {
        this.instance = plugin;
        this.instance.getManagers().add(this);
    }

    public void disable() {
    }

    public void setData(ItemStack stack, int damage) {
        if (this.getInstance().getVersionManager().isVer16()) {
            if (stack.getItemMeta() == null) {
                return;
            }
            Damageable damageable = (Damageable) stack.getItemMeta();
            damageable.damage(damage);
            stack.setItemMeta((ItemMeta) damageable);
        } else {
            stack.setDurability((short) damage);
        }
    }

    public int getData(ItemStack stack) {
        if (!this.getInstance().getVersionManager().isVer16()) {
            return stack.getDurability();
        }
        if (stack.getItemMeta() == null) {
            return 0;
        }
//        Damageable damageable = (Damageable)stack.getItemMeta();
//        return damageable.getDamage();
        return stack.getDurability();
    }

    public void enable() {
    }

    public void setItemInHand(Player player, ItemStack stack) {
        this.getInstance().getVersionManager().getVersion().setItemInHand(player, stack);
        player.updateInventory();
    }

    public HCF getInstance() {
        return this.instance;
    }

    public void registerListener(Listener listener) {
        this.instance.getServer().getPluginManager().registerEvents(listener, this.instance);
    }

    public void takeItemInHand(Player player, int HCF) {
        ItemStack stack = this.getItemInHand(player);
        if (stack == null) {
            return;
        }
        if (stack.getAmount() <= 1) {
            this.setItemInHand(player, new ItemStack(Material.AIR));
        } else {
            stack.setAmount(stack.getAmount() - HCF);
        }
        player.updateInventory();
    }

    public ItemStack getItemInHand(Player player) {
        ItemStack stack = this.getInstance().getVersionManager().getVersion().getItemInHand(player);
        if (stack != null && stack.getType() == Material.AIR) {
            return null;
        }
        return stack;
    }
}
