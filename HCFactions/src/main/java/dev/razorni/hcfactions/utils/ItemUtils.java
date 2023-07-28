package dev.razorni.hcfactions.utils;

import dev.razorni.hcfactions.extras.framework.extra.Configs;
import dev.razorni.hcfactions.utils.menuapi.GlowEnchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.TreeMap;

public class ItemUtils {
    private static final Map<String, Material> MATERIALS;
    public static Enchantment FAKE_GLOW = new GlowEnchantment(70);

    static {
        MATERIALS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    private final Configs configs;

    public ItemUtils(Configs configs) {
        this.configs = configs;
        this.load();
    }

    public static void giveItem(Player player, ItemStack stack, Location location) {
        if (player.getInventory().firstEmpty() == -1) {
            for (ItemStack content : player.getInventory().getContents()) {
                if (!stack.isSimilar(content) || content.getAmount() >= content.getMaxStackSize()) continue;
                int amount = content.getAmount() + stack.getAmount();
                if (amount <= content.getMaxStackSize()) {
                    content.setAmount(amount);
                    return;
                }
                content.setAmount(content.getMaxStackSize());
                stack.setAmount(amount - content.getMaxStackSize());
            }
            player.getWorld().dropItemNaturally(location, stack);
        } else {
            player.getInventory().addItem(stack);
            player.updateInventory();
        }
    }

    public static void tryFit(Player p, ItemStack item) {
        PlayerInventory inv = p.getInventory();
        boolean canfit = false;
        for (int i = 0; i < inv.getSize(); ++i) {
            if (inv.getItem(i) == null || inv.getItem(i) != null && inv.getItem(i).getType() == Material.AIR) {
                canfit = true;
                inv.addItem(item);
                break;
            }
        }
        if (!canfit) {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
        }
    }

    public static Material getMat(String input) {
        Material material = ItemUtils.MATERIALS.get(input);
        if (material == null) {
            throw new IllegalArgumentException("The material : " + input + " is incorrect!");
        }
        return material;
    }

    public static String getItemName(ItemStack stack) {
        if (stack == null) {
            return "Hand";
        }
        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            return stack.getItemMeta().getDisplayName();
        }
        Material material = stack.getType();
        if (material == Material.DIAMOND_SWORD) {
            return "Diamond Sword";
        }
        if (material == Material.DIAMOND_PICKAXE) {
            return "Diamond Pickaxe";
        }
        if (material == Material.DIAMOND_AXE) {
            return "Diamond Axe";
        }
        if (material == Material.DIAMOND_SPADE) {
            return "Diamond Shovel";
        }
        if (material == Material.STICK) {
            return CC.translate("&cRazorni's 20cm Dick");
        }
        return (material == Material.AIR) ? "Hand" : material.name();
    }

    public static ItemStack renameItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public void load() {
        for (String s : this.configs.getItemsConfig().getKeys(false)) {
            String[] stringArray;
            if (Utils.getNMSVer().equalsIgnoreCase("1_8_R3") && s.equalsIgnoreCase("LOCKED_CHEST")) continue;
            String ss = this.configs.getItemsConfig().getString(s);
            if (ss.contains(";")) {
                stringArray = ss.split(";");
            } else {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = ss;
            }
            for (String g : stringArray) {
                MATERIALS.put(g, Material.valueOf(s));
            }
        }
    }
}
