package dev.razorni.hub.utils.shits;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;

public class ItemUtils {
    private static final Map<String, Material> MATERIALS;


    static {
        MATERIALS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
        return (material == Material.AIR) ? "Hand" : material.name();
    }
}
