package dev.razorni.gkits.customenchant.menu;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ShopMenu extends Menu {

    private final GKits arsenic;

    @Override
    public String getTitle(Player player) {
        return "Enchantments Shop";
    }

    @Override
    public int getSize() {
        return 63;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        GKits.get().getCustomEnchantManager().getCustomEnchantList().forEach(customEnchant ->
                buttonMap.put(29, new EnchantButton(customEnchant))
        );
        GKits.get().getCustomEnchantManager().getCustomEnchantList().forEach(customEnchant ->
                buttonMap.put(30, new EnchantButton(GKits.get().getCustomEnchantManager().getCustomEnchantList().get(1)))
        );
        GKits.get().getCustomEnchantManager().getCustomEnchantList().forEach(customEnchant ->
                buttonMap.put(31, new EnchantButton(GKits.get().getCustomEnchantManager().getCustomEnchantList().get(2)))
        );
        GKits.get().getCustomEnchantManager().getCustomEnchantList().forEach(customEnchant ->
                buttonMap.put(32, new EnchantButton(GKits.get().getCustomEnchantManager().getCustomEnchantList().get(3)))
        );
        GKits.get().getCustomEnchantManager().getCustomEnchantList().forEach(customEnchant ->
                buttonMap.put(33, new EnchantButton(GKits.get().getCustomEnchantManager().getCustomEnchantList().get(0)))
        );
        buttonMap.put(0, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(1, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(7, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(8, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(9, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(17, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(45, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(53, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(54, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(55, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(61, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(62, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(10)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        return buttonMap;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
    }

    private class EnchantButton extends Button {

        private final CustomEnchant customEnchant;
        private final int price;

        public EnchantButton(CustomEnchant customEnchant) {
            this.customEnchant = customEnchant;
            this.price = arsenic.getGKitConfig().getEnchantPrice(customEnchant);
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDisplayName(ChatColor.GREEN + customEnchant.getName())
                    .setData(14)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .setLore(CC.MENU_BAR, ChatColor.LIGHT_PURPLE + "Price" + ChatColor.GOLD + " » " + ChatColor.WHITE + price + " Levels", ChatColor.LIGHT_PURPLE + "Works on" + ChatColor.GOLD + " » " + ChatColor.WHITE + customEnchant.getBookClone(), " ", "" + CC.translate(customEnchant.getDescription()), CC.MENU_BAR)
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (price < 0) {
                player.sendMessage(ChatColor.RED + "This enchant has an invalid price set. Please contact " +
                        "the server administration.");
                return;
            }

            int level = player.getLevel();
            if (level < price) {
                player.sendMessage(ChatColor.RED + "You cannot afford this enchant. You need " + ChatColor.BOLD
                        + (price - level) + ChatColor.RED + " more levels.");
                return;
            }

            boolean added = player.getInventory().addItem(customEnchant.getBook().clone()).size() <= 0;
            if (!added) {
                player.sendMessage(ChatColor.RED + "Your inventory is full.");
                return;
            }

            player.setLevel(player.getLevel() - price);
            player.sendMessage(CC.format("&eYou have purchased &d1x %s &efor &d%d&e levels.",
                    customEnchant.getDisplayName(), price));
        }
    }

}
