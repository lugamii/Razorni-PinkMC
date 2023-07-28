package dev.razorni.hcfactions.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@UtilityClass
public class ShopUtils {

    public int getEmptySlots(Inventory inventory) {
        return (int) Arrays.stream(inventory.getContents())
                .filter(itemStack -> itemStack == null || itemStack.getType() == Material.AIR).count();
    }

    public boolean containAmount(Player player, ItemStack itemStack, int amount) {
        return player.getInventory().contains(itemStack.getType(), amount);
    }

    public void removeItem(Player player, ItemStack itemStack, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemStack.getType()) {
                if (item.getAmount() == amount) {
                    player.getInventory().removeItem(item);
                    break;
                }

                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                    break;
                }

                if (item.getAmount() < amount) {
                    amount -= item.getAmount();
                    player.getInventory().removeItem(item);
                }
            }
        }
    }

    public void removeAll(Player player, ItemStack itemStack, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemStack.getType()) {
                if (item.getAmount() == amount) {
                    player.getInventory().removeItem(item);
                    continue;
                }

                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                    continue;
                }

                if (item.getAmount() < amount) {
                    amount -= item.getAmount();
                    player.getInventory().removeItem(item);
                }
            }
        }
    }

    public int getPrice(ItemStack item, int amount) {
        if (item.getType() == Material.DIAMOND_BLOCK) {
            return (int) ((250D / amount) * item.getAmount());
        } else if (item.getType() == Material.GOLD_BLOCK) {
            return (int) ((210D / amount) * item.getAmount());
        } else if (item.getType() == Material.IRON_BLOCK) {
            return (int) ((230D / amount) * item.getAmount());
        } else if (item.getType() == Material.COAL_BLOCK) {
            return (int) ((230D / amount) * item.getAmount());
        } else if (item.getType() == Material.LAPIS_BLOCK) {
            return (int) ((250D / amount) * item.getAmount());
        } else if (item.getType() == Material.EMERALD_BLOCK) {
            return (int) ((210D / amount) * item.getAmount());
        } else if (item.getType() == Material.REDSTONE_BLOCK) {
            return (int) ((250D / amount) * item.getAmount());
        } else if (item.getType() == Material.COBBLESTONE) {
            return (int) ((50D / amount) * item.getAmount());
        }

        return 0;
    }
}