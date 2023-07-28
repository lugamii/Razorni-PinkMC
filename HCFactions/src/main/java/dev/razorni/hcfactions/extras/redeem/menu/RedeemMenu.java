package dev.razorni.hcfactions.extras.redeem.menu;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.redeem.RedeemManager;
import dev.razorni.hcfactions.extras.redeem.RedeemablePartner;
import dev.razorni.hcfactions.users.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RedeemMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW + "Support";
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(1, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(2, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(3, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(4, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(5, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(0, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(6, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(7, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(8, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(18, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(19, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(20, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(21, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(22, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(23, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(24, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(25, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });
        buttons.put(26, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setData(5)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .build();
            }
        });


        int i = 9;
        for (RedeemablePartner partner : RedeemManager.getRedeemablePartners()) {
            buttons.put(i++, new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM, 3)
                            .setDisplayName(CC.PINK + partner.getName())
                            .setLore(ChatColor.YELLOW + "You will receive " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "x1 Partner key!", ChatColor.RED + "❤ " + ChatColor.YELLOW + "Right click to vote")
                            .setSkullOwner(partner.getName())
                            .build();
                }

                @Override
                public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
                    User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());

                    if (user.isRedeemed()) {
                        player.sendMessage(CC.translate(HCF.getPlugin().getConfig().getString("REDEEM.ALREADY-REDEEMED")));
                        return;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " partner 1");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " Christmas 1");
                    Bukkit.broadcastMessage(CC.translate("&3&l[REDEEM] " + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName() + " &fhas supported &3" + partner.getName() + " &fand obtained their rewards using command " + "&3/redeem"));
                    player.closeInventory();
                    for (String s : HCF.getPlugin().getConfig().getStringList("REDEEM.SUCCESSFUL-MESSAGE")) {
                        player.sendMessage(CC.translate(s).replace("%partner%", partner.getName()));
                    }
                    user.setRedeemed(true);
                }
            });
        }

        return buttons;
    }

}
