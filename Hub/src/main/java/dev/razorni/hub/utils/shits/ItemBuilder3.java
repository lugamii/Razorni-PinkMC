package dev.razorni.hub.utils.shits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 06.12.2018 / 18:26
 * BuilderAPI / de.langgezockt.BuilderAPI
 */

public class ItemBuilder3 implements Cloneable {

    public static final Enchantment GLOW = new EnchantmentWrapper(-1) {
        @Override
        public String getName() {
            return "glow";
        }

        @Override
        public int getMaxLevel() {
            return 1;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ALL;
        }

        @Override
        public boolean conflictsWith(Enchantment other) {
            return false;
        }

        @Override
        public boolean canEnchantItem(ItemStack item) {
            return true;
        }
    };

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder3(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder3(Material material, short subID) {
        this.itemStack = new ItemStack(material, 1, subID);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder3(Material material, int subID) {
        this.itemStack = new ItemStack(material, 1, (short) subID);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder3(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder3 setDisplayName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder3 setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder3 setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder3 addToLore(String... entries) {
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.addAll(Arrays.asList(entries));
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder3 addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder3 storeEnchantment(Enchantment enchantment, int level) {
        if (this.itemMeta instanceof EnchantmentStorageMeta)
            ((EnchantmentStorageMeta) this.itemMeta).addStoredEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder3 glowing(boolean glowing) {
        if (glowing)
            itemMeta.addEnchant(GLOW, 1, true);
        else itemMeta.removeEnchant(GLOW);
        return this;
    }

    public ItemBuilder3 setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder3 setUnbreakable(Boolean unbreakable) {
        this.itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder3 setSkullOwner(String owner) {
        if (this.itemMeta instanceof SkullMeta)
            ((SkullMeta) this.itemMeta).setOwner(owner);
        return this;
    }

    public ItemBuilder3 setArmorColor(Color color) {
        if (this.itemMeta instanceof LeatherArmorMeta)
            ((LeatherArmorMeta) this.itemMeta).setColor(color);
        return this;
    }

    public ItemBuilder3 setData(short data) {
        this.itemStack.setDurability(data);
        return this;
    }

    public ItemBuilder3 setData(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    public ItemBuilder3 setBookAuthor(String author) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setAuthor(author);
        return this;
    }

    public ItemBuilder3 setBookTitle(String title) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setTitle(title);
        return this;
    }

    public ItemBuilder3 setBookPages(String... pages) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setPages(pages);
        return this;
    }

    public ItemBuilder3 setBookPages(List<String> pages) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setPages(pages);
        return this;
    }

    public ItemBuilder3 addFlag(ItemFlag... flags) {
        this.itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return itemStack.clone();
    }

    public ItemBuilder3 clone() {
        return new ItemBuilder3(build());
    }
}
