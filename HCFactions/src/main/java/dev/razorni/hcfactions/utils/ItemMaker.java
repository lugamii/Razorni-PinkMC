package dev.razorni.hcfactions.utils;

/**
 * Created By LeandroSSJ
 * Created on 14/06/2021
 */

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Data
@Setter
public class ItemMaker implements Cloneable {

    @Getter
    private Material type;
    private int data;
    private int amount;
    private String title;

    @Setter
    private List<String> lore;
    private Color color;

    @Setter
    private HashMap<Enchantment, Integer> enchantments;
    private boolean unbreakable;

    public ItemMaker(Material type) {
        this(type, 1);
    }

    public ItemMaker(Material type, int amount) {
        this(type, amount, 0);
    }

    public ItemMaker(Material type, int amount, int data) {
        this.lore = new ArrayList<String>();

        this.type = type;
        this.amount = amount;
        this.data = data;

        this.enchantments = new HashMap<Enchantment, Integer>();
    }

    public ItemMaker(ItemStack itemStack) {
        Validate.notNull(itemStack, "ItemStack cannot be null");

        this.lore = new ArrayList<String>();
        this.enchantments = new HashMap<Enchantment, Integer>();

        this.type = itemStack.getType();
        this.data = itemStack.getDurability();
        this.amount = itemStack.getAmount();

        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasDisplayName()) this.title = itemStack.getItemMeta().getDisplayName();

            if (itemStack.getItemMeta().hasLore()) this.lore = itemStack.getItemMeta().getLore();
        }
        if (itemStack.getEnchantments() != null) this.enchantments.putAll(itemStack.getEnchantments());

        if (itemStack.getType().toString().toLowerCase().contains("leather") && itemStack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
            this.color = lam.getColor();
        }
    }

    public ItemMaker(ItemMaker itemMaker) {
        this(itemMaker.build());
    }

    public ItemMaker setUnbreakable(boolean flag) {
        this.unbreakable = flag;
        return this;
    }

    public ItemMaker addLore(String... lores) {
        for (String lore : lores) {
            this.lore.add(CC.translate(lore));
        }
        return this;
    }

    public ItemMaker setData(int data) {
        this.data = data;
        return this;
    }

    public ItemMaker setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemMaker setName(String title) {
        this.title = CC.translate(title);
        return this;
    }

    public ItemMaker setSkullType(SkullType skullType) {
        Validate.notNull(skullType, "SkullType cannot be null");

        this.setData(skullType.data);
        return this;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemMaker setLore(String... lore) {
        this.lore = CC.translate(Arrays.asList(lore));
        return this;
    }

    public ItemMaker setLore(List<String> list) {
        this.lore = CC.translate(list);
        return this;
    }

    public Material getType() {
        return this.type;
    }

    public ItemMaker setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemMaker addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemMaker setColor(Color color) {
        if (!this.type.toString().toLowerCase().contains("leather"))
            throw new RuntimeException("Cannot set color of non-leather items.");

        this.color = color;
        return this;
    }

    public ItemStack build() {
        Validate.noNullElements(new Object[]{this.type, this.data, this.amount});

        ItemStack itemStack = new ItemStack(this.type, this.amount, (short) this.data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (this.title != null && this.title != "") itemMeta.setDisplayName(this.title);

        if (this.lore != null && !this.lore.isEmpty()) itemMeta.setLore(this.lore);

        if (this.color != null && this.type.toString().toLowerCase().contains("leather"))
            ((LeatherArmorMeta) itemMeta).setColor(this.color);

        itemStack.setItemMeta(itemMeta);
        if (this.enchantments != null && !this.enchantments.isEmpty())
            itemStack.addUnsafeEnchantments(this.enchantments);

        if (this.unbreakable) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.spigot().setUnbreakable(true);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public ItemMaker clone() {
        return new ItemMaker(this);
    }

    public enum SkullType {

        SKELETON("SKELETON", 0, 0),
        WITHER_SKELETON("WITHER_SKELETON", 1, 1),
        ZOMBIE("ZOMBIE", 2, 2),
        PLAYER("PLAYER", 3, 3),
        CREEPER("CREEPER", 4, 4);

        private final int data;

        SkullType(String s, int n, int data) {
            this.data = data;
        }

        public int getData() {
            return this.data;
        }
    }
}
