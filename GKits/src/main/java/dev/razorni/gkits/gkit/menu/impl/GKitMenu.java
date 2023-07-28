package dev.razorni.gkits.gkit.menu.impl;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.profile.Profile;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
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
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GKitMenu extends Menu {

    private static final Logger LOG = GKits.get().getLogFactory().newLogger("Cooldown");

    private final GKits plugin;
    private final Profile profile;

    @Override
    public String getTitle(Player player) {
        return CC.YELLOW + "GKits";
    }

    @Override
    public int getSize() {
        return plugin.getGKitConfig().getInventorySize();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(0, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(1, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(7, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(8, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(9, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(17, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(18, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(19, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(25, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttonMap.put(26, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(2)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });

        plugin.getGKitMenuConfig().getKitMenuItemList().forEach(gKitMenuItem ->
                buttonMap.put(gKitMenuItem.getSlot(), new Button() {
                    @Override
                    public ItemStack getItem(Player player) {
                        return gKitMenuItem.getItemStack();
                    }

                    @Override
                    public boolean isCancelClick() {
                        return true;
                    }
                }));

        plugin.getGKitManager().getKitMap().values().forEach(gKit -> {

            if (gKit.getIcon() == null
                    || gKit.getIcon().getType() == Material.AIR)
                return;

            buttonMap.put(gKit.getSlot(), new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(CC.MENU_BAR);
                    if (!player.hasPermission("secondlife.gkit." + gKit.getName())) {
                        lore.add(CC.RED + "You don't own this kit");
                        lore.add(CC.translate("&cPurchase at &e&ostore.hcfactions.net"));
                        lore.add(" ");
                        lore.add(CC.translate("&7&l(&6&l!&7&l)&r &dRight click to preview"));
                    } else {
                            lore.add(CC.translate("&dCooldown &6» &r" + profile.formatRemaining(gKit)));
                            lore.add("");
                            lore.add(CC.translate("&7&l(&6&l!&7&l)&r &dRight click to preview"));

                    }
                    lore.add(CC.MENU_BAR);


                    return new ItemBuilder(gKit.getIcon())
                            .setDisplayName(CC.GREEN + gKit.getName())
                            .setLore(CC.translate(lore))
                            .build();
                }

                @Override
                public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if (clickType.isRightClick()) {
                        new GKitPreviewMenu(gKit, plugin).openMenu(player);
                        return;
                    }

                    if (player.hasPermission("secondlife.cooldown.bypass") && clickType.isShiftClick()) {
                        LOG.info(player.getName() + " has bypassed " + gKit.getName() + " cooldown.");
                        gKit.apply(player);
                        return;
                    }

                    if (!player.hasPermission("secondlife.gkit." + gKit.getName())) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use "
                                + gKit.getName() + " gkit.");
                        return;
                    }

                    if (profile.isOnCooldown(gKit)) {
                        player.sendMessage(ChatColor.RED + "You are still on cooldown " +
                                "on this gkit for another " + ChatColor.BOLD
                                + TimeUtils.formatDetailed(profile.getCooldown(gKit)) + ChatColor.RED + ".");
                        return;
                    }

                    player.closeInventory();
                    profile.applyCooldown(gKit);
                    gKit.apply(player);
                }
            });
        });

        return buttonMap;
    }
}
