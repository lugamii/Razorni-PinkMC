package dev.razorni.hub.utils.shits;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder2 {

    private ItemStack stack;
    private ItemMeta meta;

    private List<String> lores = new ArrayList<>();

    /**
     * Creates a new instance with a given material
     * and a default quantity of 1.
     *
     * @param material the material to create from
     */
    public ItemBuilder2(Material material) {
        this(material, 1);
    }

    /**
     * Creates a new instance with a given material and quantity.
     *
     * @param material the material to create from
     * @param amount   the quantity to build with
     */
    public ItemBuilder2(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    /**
     * Creates a new instance with a given {@link ItemStack}.
     *
     * @param stack the stack to create from
     */
    public ItemBuilder2(ItemStack stack) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.stack = stack;
    }

    /**
     * Creates a new instance with a given material, quantity and data.
     *
     * @param material the material to create from
     * @param amount   the quantity to build with
     * @param data     the data to build with
     */
    public ItemBuilder2(Material material, int amount, byte data) {
        Preconditions.checkNotNull(material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.stack = new ItemStack(material, amount, data);
    }

    /**
     * Sets the display name of this item builder.
     *
     * @param name the display name to set
     * @return this instance
     */
    public ItemBuilder2 displayName(String name) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    /**
     * Adds a line to the lore of this builder at a specific position.
     *
     * @param line the line to add
     * @return this instance
     */
    public ItemBuilder2 loreLine(String line) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        lores.add(line);
        return this;
    }

    /**
     * Sets the lore of this item builder.
     *
     * @param lore the lore varargs to set
     * @return this instance
     */
    public ItemBuilder2 lore(String... lore) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder2 loreList(List<String> lore) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }
        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        this.meta.setLore(lores);
        return this;
    }

    /**
     * @see ItemBuilder2#enchant(Enchantment, int, boolean)
     */
    public ItemBuilder2 enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, true);
    }

    /**
     * Adds an enchantment to this item builder.
     *
     * @param enchantment the enchant to add
     * @param level       the level to add at
     * @param unsafe      if it should use unsafe calls
     * @return this instance
     */
    public ItemBuilder2 enchant(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            stack.addUnsafeEnchantment(enchantment, level);
        } else {
            stack.addEnchantment(enchantment, level);
        }

        return this;
    }

    public ItemBuilder2 amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    /**
     * Sets the data of this item builder.
     *
     * @param data the data value to set
     * @return the updated item builder
     */
    public ItemBuilder2 data(short data) {
        stack.setDurability(data);
        return this;
    }

    /**
     * Builds this into an {@link ItemStack}.
     *
     * @return the built {@link ItemStack}
     */
    public ItemStack build() {
        if (meta != null) {
            if (!lores.isEmpty()) {
                meta.setLore(lores);
            }
            stack.setItemMeta(meta);
        }

        return stack;
    }
}
