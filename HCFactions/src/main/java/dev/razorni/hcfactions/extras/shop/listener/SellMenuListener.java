package dev.razorni.hcfactions.extras.shop.listener;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class SellMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!event.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.INVENTORY-NAME"))) {
            return;
        }

        if (event.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.INVENTORY-NAME"))) {
            Material sellmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.ITEMS.SELLALL.MATERIAL"));
            if (event.getCurrentItem().getType().equals(sellmaterial)) {
                PlayerInventory inv = player.getInventory();
                double total = 0;

                for (int i = 0; i < inv.getSize(); ++i) {
                    if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
                        continue;
                    }
                    switch (inv.getItem(i).getType()) {
                        case GOLD_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.GOLD-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case DIAMOND_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.DIAMOND-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case IRON_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.IRON-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case REDSTONE_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.REDSTONE-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case LAPIS_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.LAPIS-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case EMERALD_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.EMERALD-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case COAL_BLOCK: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.BLOCKS.COAL-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        // Materials
                        case COAL: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.COAL-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case GOLD_INGOT: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.GOLD-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case IRON_INGOT: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.IRON-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case EMERALD: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.EMERALD-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case DIAMOND: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.DIAMOND-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        case REDSTONE: {
                            if (inv.getItem(i).getItemMeta().hasEnchants())
                                return;
                            double amount = HCF.getPlugin().getConfig().getDouble("SELL-ALL.MATERIALS.REDSTONE-PRICE");
                            double realamm = amount * inv.getItem(i).getAmount();
                            HCF.getPlugin().getBalanceManager().giveBalance(player, (int) realamm);
                            inv.setItem(i, null);
                            total = total + realamm;
                            break;
                        }
                        default:
                            break;
                    }
                    player.updateInventory();
                }
                if (total > 0) {
                    player.sendMessage(CC.translate("&eYou`ve sold all &dvaluables &efor &d$" + total + "&f."));
                } else {
                    player.sendMessage(CC.translate("&cYou dont have any valuables on you."));
                }
            }
        }
    }
}
