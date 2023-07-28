//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dev.razorni.core.util;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.razorni.core.Core;
import dev.razorni.core.util.general.GlowEnchantment;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
    private static final Map<String, ItemUtils.ItemData> NAME_MAP = new HashMap();
    public static Enchantment FAKE_GLOW = new GlowEnchantment(70);

    public ItemUtils() {
    }

    public static void load() {
        NAME_MAP.clear();
        List<String> lines = readLines();

        for (String line : lines) {
            String[] parts = line.split(",");
            NAME_MAP.put(parts[0], new ItemData(Material.getMaterial(Integer.parseInt(parts[1])), Short.parseShort(parts[2])));
        }

    }

    public static void setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemUtils.ItemBuilder builder(Material type) {
        return new ItemUtils.ItemBuilder(type);
    }

    public static ItemStack get(String input, int amount) {
        ItemStack item = get(input);
        if (item != null) {
            item.setAmount(amount);
        }

        return item;
    }

    public static ItemStack get(String input) {
        if (NumberUtils.isInteger(input = input.toLowerCase().replace(" ", ""))) {
            return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
        } else if (input.contains(":")) {
            if (NumberUtils.isShort(input.split(":")[1])) {
                if (NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1, Short.parseShort(input.split(":")[1]));
                } else if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                    return null;
                } else {
                    ItemUtils.ItemData data = (ItemUtils.ItemData)NAME_MAP.get(input.split(":")[0].toLowerCase());
                    return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
                }
            } else {
                return null;
            }
        } else {
            return !NAME_MAP.containsKey(input) ? null : ((ItemUtils.ItemData)NAME_MAP.get(input)).toItemStack();
        }
    }

    public static String getName(ItemStack item) {
        String name = item.getType().name();
        if (name.contains(".")) {
            name = WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
        }

        return name;
    }

    private static List<String> readLines() {
        try {
            return IOUtils.readLines(Core.class.getClassLoader().getResourceAsStream("items.csv"));
        } catch (IOException var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static class ItemData {
        private final Material material;
        private final short data;

        public String getName() {
            return ItemUtils.getName(this.toItemStack());
        }

        public boolean matches(ItemStack item) {
            return item != null && item.getType() == this.material && item.getDurability() == this.data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(this.material, 1, this.data);
        }

        public Material getMaterial() {
            return this.material;
        }

        public short getData() {
            return this.data;
        }

        @ConstructorProperties({"material", "data"})
        public ItemData(Material material, short data) {
            this.material = material;
            this.data = data;
        }
    }

    public static final class ItemBuilder {
        private Material type;
        private int amount;
        private short data;
        private String name;
        private List<String> lore;
        private final Map<Enchantment, Integer> enchantments;

        private ItemBuilder(Material type) {
            this.amount = 1;
            this.data = 0;
            this.lore = new ArrayList();
            this.enchantments = new HashMap();
            this.type = type;
        }

        public ItemUtils.ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemUtils.ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemUtils.ItemBuilder data(short data) {
            this.data = data;
            return this;
        }

        public ItemUtils.ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemUtils.ItemBuilder addLore(String... lore) {
            this.lore.addAll(Arrays.asList(lore));
            return this;
        }

        public ItemUtils.ItemBuilder addLore(int index, String lore) {
            this.lore.set(index, lore);
            return this;
        }

        public ItemUtils.ItemBuilder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemUtils.ItemBuilder enchant(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemUtils.ItemBuilder unenchant(Enchantment enchantment) {
            this.enchantments.remove(enchantment);
            return this;
        }

        public ItemStack build() {
            ItemStack item = new ItemStack(this.type, this.amount, this.data);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
            ArrayList<String> finalLore = new ArrayList();

            for(int index = 0; index < this.lore.size(); ++index) {
                if (this.lore.get(index) == null) {
                    finalLore.set(index, "");
                } else {
                    finalLore.set(index, ChatColor.translateAlternateColorCodes('&', (String)this.lore.get(index)));
                }
            }

            meta.setLore(finalLore);
            Iterator var6 = this.enchantments.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<Enchantment, Integer> entry = (Entry)var6.next();
                item.addUnsafeEnchantment((Enchantment)entry.getKey(), (Integer)entry.getValue());
            }

            item.setItemMeta(meta);
            return item;
        }
    }
}
