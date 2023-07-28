package dev.razorni.hub.utils.shits;

import com.google.common.base.Preconditions;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {


    private ItemStack stack;
    private ItemMeta meta;

    /**
     * Creates a new instance with a given material
     * and a default quantity of 1.
     *
     * @param material the material to create from
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    public ItemBuilder(ItemStack stack) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.stack = stack;
    }

    public ItemBuilder(Material material, int amount, byte data) {
        Preconditions.checkNotNull(material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.stack = new ItemStack(material, amount, data);
    }

    public ItemBuilder displayName(String name) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder loreLine(String line) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        boolean hasLore = meta.hasLore();
        List<String> lore = hasLore ? meta.getLore() : new ArrayList<>();
        lore.add(hasLore ? lore.size() : 0, line);

        this.lore(line);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }

        meta.setLore(lore);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, true);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            stack.addUnsafeEnchantment(enchantment, level);
        } else {
            stack.addEnchantment(enchantment, level);
        }

        return this;
    }

    public ItemBuilder data(int data) {
        stack.setDurability((short) data);
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            stack.setItemMeta(meta);
        }

        return stack;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if (!this.stack.getType().equals(Material.WOOL)) {
            return this;
        }
        this.stack.setDurability(color.getData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) this.stack.getItemMeta();
            im.setColor(color);
            this.stack.setItemMeta(im);
        } catch (ClassCastException ignored) {

        }
        return this;
    }
}
