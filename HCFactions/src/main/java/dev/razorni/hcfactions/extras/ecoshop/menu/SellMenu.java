package dev.razorni.hcfactions.extras.ecoshop.menu;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.utils.menuapi.menu.button.BackButton;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ecoshop.buttons.SellItemButton;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.ShopUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellMenu extends Menu {

    final static int amount = 64;

    @Override
    public String getTitle(Player player) {
        return CC.translate("&dSell Shop &7┃ &f$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
    }

    @Override
    public int size(Player player) {
        return 9 * 3;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(18, new GlassButton());
        buttons.put(19, new GlassButton());
        buttons.put(25, new GlassButton());
        buttons.put(26, new GlassButton());

        buttons.put(4, new PotionMenu.HeadButton());
        buttons.put(21, new BackButton(new CategorySelectorMenu()));
        buttons.put(23, new SellAllButton());

        buttons.put(10,
                new SellItemButton("&dDiamond Block", 250, amount, new ItemStack(Material.DIAMOND_BLOCK)));
        buttons.put(11,
                new SellItemButton("&dGold Block", 210, amount, new ItemStack(Material.GOLD_BLOCK)));
        buttons.put(12,
                new SellItemButton("&dIron Block", 230, amount, new ItemStack(Material.IRON_BLOCK)));

        buttons.put(13,
                new SellItemButton("&dCoal Block", 230, amount, new ItemStack(Material.COAL_BLOCK)));
        buttons.put(14,
                new SellItemButton("&dLapis Block", 250, amount, new ItemStack(Material.LAPIS_BLOCK)));
        buttons.put(15,
                new SellItemButton("&dEmerald Block", 210, amount, new ItemStack(Material.EMERALD_BLOCK)));
        buttons.put(16,
                new SellItemButton("&dRedstone Block", 250, amount,
                        new ItemStack(Material.REDSTONE_BLOCK)));

        return buttons;
    }

    @AllArgsConstructor
    public static class SellAllButton extends Button {


        public String getName(Player player) {
            return CC.PINK + "Sell Inventory";
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ANVIL, 1);
        }

        public String getLore(Player player) {
            return CC.translate("&fSell everything from your inventory.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Material[] toSell = new Material[]{
                    Material.DIAMOND_BLOCK,
                    Material.GOLD_BLOCK,
                    Material.IRON_BLOCK,
                    Material.COAL_BLOCK,
                    Material.LAPIS_BLOCK,
                    Material.EMERALD_BLOCK,
                    Material.REDSTONE_BLOCK,
                    Material.COBBLESTONE
            };

            int price = 0;

            int itemsSold = 0;

            List<ItemStack> toSellList = new ArrayList<>();

            for (Material item : toSell) {
                if (player.getInventory().all(item).size() > 0) {
                    HashMap<Integer, ? extends ItemStack> map = player.getInventory().all(item);

                    for (ItemStack stack : map.values()) {
                        price += ShopUtils.getPrice(stack, amount);
                        itemsSold += stack.getAmount();
                        toSellList.add(stack);
                    }
                }
            }

            if (price == 0) {
                player.sendMessage(ChatColor.RED + "You don't have anything to sell!");
                Button.playFail(player);
                return;
            }

            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            profile.setBalance(profile.getBalance() + price);

            new CategorySelectorMenu().openMenu(player);
            new SellMenu().openMenu(player);

            for (ItemStack item : toSellList) {
                player.getInventory().removeItem(item);
            }

            player.sendMessage(
                    CC.translate("&aYou sold " + itemsSold + " &aitems for $" + price + ""));
            Button.playSuccess(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class GlassButton extends Button {


        public String getName(Player player) {
            return CC.translate(" ");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

}
