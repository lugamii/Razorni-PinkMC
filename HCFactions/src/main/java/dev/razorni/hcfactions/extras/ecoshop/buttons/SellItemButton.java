package dev.razorni.hcfactions.extras.ecoshop.buttons;

import com.google.common.collect.Lists;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ecoshop.menu.CategorySelectorMenu;
import dev.razorni.hcfactions.extras.ecoshop.menu.SellMenu;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.ShopUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class SellItemButton extends Button {

    final String name;
    final int cost;
    final int amount;
    final ItemStack itemStack;

    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + name;
    }

    public List<String> getDescription(Player player) {
        return Lists.newArrayList(
                "&fPrice: &d$" + cost,
                "",
                "&fRight Click to sell &d" + amount + " &fblock",
                "&aLeft Click&f to sell one block",
                "&cShift Right Click&f to sell all blocks"
        );
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(itemStack).name(getName(player))
                .lore(getDescription(player)).amount(amount).build();
    }

    public Material getMaterial(Player player) {
        return itemStack.getType();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        if (clickType == ClickType.SHIFT_RIGHT) {
            Material material = itemStack.getType();

            int price = 0;

            int itemsSold = 0;

            List<ItemStack> toSellList = new ArrayList<>();

            if (player.getInventory().all(material).size() > 0) {
                HashMap<Integer, ? extends ItemStack> map = player.getInventory().all(material);

                for (ItemStack stack : map.values()) {
                    price += ShopUtils.getPrice(stack, amount);
                    itemsSold += stack.getAmount();
                    toSellList.add(stack);
                }
            }

            if (price == 0) {
                player.sendMessage(ChatColor.RED + "You don't have anything to sell!");
                Button.playFail(player);
                return;
            }

            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());

            profile.setBalance(profile.getBalance() + price);
            profile.save();

            new CategorySelectorMenu().openMenu(player);
            new SellMenu().openMenu(player);

            for (ItemStack item : toSellList) {
                player.getInventory().removeItem(item);
            }

            player.sendMessage(
                    CC.translate("&aYou sold " + itemsSold + " &aitems for $" + price + ""));
            Button.playSuccess(player);
        } else if (clickType == ClickType.RIGHT) {
            if (ShopUtils.containAmount(player, itemStack, amount)) {
                ShopUtils.removeItem(player, itemStack, amount);
                player.updateInventory();
                player.sendMessage(ChatColor.GREEN + "Sold " + amount + " " + itemStack.getType().toString().toLowerCase().replace("_", " ") + " for $" + cost);
                User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());

                new CategorySelectorMenu().openMenu(player);
                new SellMenu().openMenu(player);

                profile.setBalance(profile.getBalance() + cost);
                profile.save();

                Button.playSuccess(player);
            } else {
                player.sendMessage(
                        ChatColor.RED + "You do not have enough " + itemStack.getType().toString().toLowerCase()
                                .replace("_", " ") + " to sell.");
                Button.playFail(player);
            }
        } else {
            if (ShopUtils.containAmount(player, itemStack, 1)) {
                int newCost = cost / amount;
                ShopUtils.removeItem(player, itemStack, 1);
                player.updateInventory();
                player.sendMessage(
                        ChatColor.GREEN + "Sold 1 " + itemStack.getType().toString().toLowerCase()
                                .replace("_", " ") + " for $" + newCost);

                User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
                profile.setBalance(profile.getBalance() + newCost);
                profile.save();

                new CategorySelectorMenu().openMenu(player);
                new SellMenu().openMenu(player);

                Button.playSuccess(player);
            } else {
                player.sendMessage(
                        ChatColor.RED + "You do not have enough " + itemStack.getType().toString().toLowerCase()
                                .replace("_", " ") + " to sell.");
                Button.playFail(player);
            }
        }
    }
}