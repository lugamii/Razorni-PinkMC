package dev.razorni.hcfactions.utils;

import dev.razorni.hcfactions.extras.framework.Manager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ItemBuilder {

    private final ItemStack is;

    public ItemBuilder(ItemStack stack) {
        this.is = stack;
    }

    public ItemBuilder(Material material, int amount, byte damage) {
        this.is = new ItemStack(material, amount, damage);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.is = new ItemStack(material, amount);
    }

    public ItemBuilder removeLoreLine(int kndex) {
        ItemMeta itemMeta = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(itemMeta.getLore());
        if (kndex < 0 || kndex > lore.size()) {
            return this;
        }
        lore.remove(kndex);
        itemMeta.setLore(lore);
        this.is.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int amplifier) {
        this.is.addUnsafeEnchantment(enchantment, amplifier);
        return this;
    }

    public ItemBuilder addLoreLine(String input, int index) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        lore.set(index, input);
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder data(Manager manager, short data) {
        manager.setData(this.is, data);
        return this;
    }

    public ItemBuilder addLoreLine(String input) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (this.is.hasItemMeta() && this.is.getItemMeta().hasLore()) {
            lore = new ArrayList<>(meta.getLore());
        }
        lore.add(CC.t(input));
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemStack toItemStack() {
        return this.is;
    }

    public ItemBuilder setSkullOwner(String input) {
        try {
            SkullMeta meta = (SkullMeta) this.is.getItemMeta();
            meta.setOwner(input);
            this.is.setItemMeta(meta);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setLore(List<String> list) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(CC.t(list));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(CC.t(name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.is.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder removeLoreLine(String string) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        if (!lore.contains(string)) {
            return this;
        }
        lore.remove(string);
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        this.is.setDurability((short) 32767);
        return this;
    }

    public ItemBuilder setLore(String... input) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(CC.t(Arrays.asList(input)));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int amplifier) {
        ItemMeta meta = this.is.getItemMeta();
        meta.addEnchant(enchantment, amplifier, true);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta(meta);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setDurability(Manager manager, short durability) {
        manager.setData(this.is, durability);
        return this;
    }

    public ItemBuilder data1(short data) {
        this.is.setDurability(data);
        return this;
    }



}
