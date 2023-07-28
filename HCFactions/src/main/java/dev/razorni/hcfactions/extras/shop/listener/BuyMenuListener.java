package dev.razorni.hcfactions.extras.shop.listener;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemMaker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BuyMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.MAIN-MENU.INVENTORY-NAME"))) {
            e.setCancelled(true);
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.INVENTORY-NAME"))) {
            e.setCancelled(true);
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.BACK-BUTTON.SLOT")) {
                p.chat("/openshop");
            }
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.INVENTORY-NAME"))) {
            e.setCancelled(true);
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.BACK-BUTTON.SLOT")) {
                p.chat("/openshop");
            }
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.INVENTORY-NAME"))) {
            e.setCancelled(true);
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.BACK-BUTTON.SLOT")) {
                p.chat("/openshop");
            }
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.MAIN-MENU.INVENTORY-NAME"))) {
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.BUY-SHOP.SLOT")) {
                Inventory buyinv;
                int size = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.SIZE");
                buyinv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.INVENTORY-NAME")));
                int glassdata = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.GLASS-COLOR");
                ItemStack glasspane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) glassdata);
                ItemMeta glassmeta = glasspane.getItemMeta();
                glassmeta.setDisplayName(" ");
                glassmeta.addEnchant(Enchantment.DURABILITY, 1, true);
                glasspane.setItemMeta(glassmeta);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.BUY-MENU.VIPER-GLASS")) {
                    buyinv.setItem(34, glasspane);
                    buyinv.setItem(33, glasspane);
                    buyinv.setItem(32, glasspane);
                    buyinv.setItem(31, glasspane);
                    buyinv.setItem(30, glasspane);
                    buyinv.setItem(29, glasspane);
                    buyinv.setItem(28, glasspane);
                }
                /* */
                Material backmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.BACK-BUTTON.MATERIAL"));
                int backdata = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.BACK-BUTTON.DATA");
                ItemStack backbutton = new ItemStack(backmaterial, 1, (short) backdata);
                ItemMeta backmeta = backbutton.getItemMeta();
                List<String> backlore = new ArrayList<String>();
                backmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.BACK-BUTTON.NAME")));
                for (String lore : HCF.getPlugin().getConfig().getStringList("SHOP.BUY-MENU.BACK-BUTTON.LORE")) {
                    backlore.add(ChatColor.translateAlternateColorCodes('&', lore));
                }
                backmeta.setLore(backlore);
                backbutton.setItemMeta(backmeta);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.BUY-MENU.BACK-BUTTON.ENABLED")) {
                    buyinv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.BACK-BUTTON.SLOT"), backbutton);
                }
                for (String sect : HCF.getPlugin().getConfig().getConfigurationSection("SHOP.BUY-MENU.ITEMS").getKeys(false)) {
                    int slot = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".SLOT");
                    int data = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".DATA");
                    int amount2 = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".AMOUNT");
                    String name = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".NAME");
                    String material = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".MATERIAL");
                    List<String> lore = HCF.getPlugin().getConfig().getStringList("SHOP.BUY-MENU.ITEMS." + sect + ".LORE");
                    ItemStack stack = new ItemMaker(Material.valueOf(material)).setData(data).setLore(CC.list(lore)).setName(CC.chat(name)).setAmount(amount2).build();
                    buyinv.setItem(slot, stack);
                }
                p.openInventory(buyinv);
            }
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SELL-SHOP.SLOT")) {
                /* */
                Inventory sellinv;
                int size2 = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.SIZE");
                sellinv = Bukkit.createInventory(null, size2, ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.INVENTORY-NAME")));
                int glassdata2 = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.GLASS-COLOR");
                ItemStack glasspane2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) glassdata2);
                ItemMeta glassmeta2 = glasspane2.getItemMeta();
                glassmeta2.addEnchant(Enchantment.DURABILITY, 1, true);
                glassmeta2.setDisplayName(" ");
                glasspane2.setItemMeta(glassmeta2);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.SELL-MENU.VIPER-GLASS")) {
                    sellinv.setItem(34, glasspane2);
                    sellinv.setItem(33, glasspane2);
                    sellinv.setItem(32, glasspane2);
                    sellinv.setItem(31, glasspane2);
                    sellinv.setItem(30, glasspane2);
                    sellinv.setItem(29, glasspane2);
                    sellinv.setItem(28, glasspane2);
                }
                /* */
                Material backmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.BACK-BUTTON.MATERIAL"));
                int backdata = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.BACK-BUTTON.DATA");
                ItemStack backbutton = new ItemStack(backmaterial, 1, (short) backdata);
                ItemMeta backmeta = backbutton.getItemMeta();
                List<String> backlore = new ArrayList<String>();
                backmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.BACK-BUTTON.NAME")));
                for (String lore : HCF.getPlugin().getConfig().getStringList("SHOP.SELL-MENU.BACK-BUTTON.LORE")) {
                    backlore.add(ChatColor.translateAlternateColorCodes('&', lore));
                }
                backmeta.setLore(backlore);
                backbutton.setItemMeta(backmeta);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.SELL-MENU.BACK-BUTTON.ENABLED")) {
                    sellinv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.BACK-BUTTON.SLOT"), backbutton);
                }
                for (String sect : HCF.getPlugin().getConfig().getConfigurationSection("SHOP.SELL-MENU.ITEMS").getKeys(false)) {
                    int slot = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.ITEMS." + sect + ".SLOT");
                    int data = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.ITEMS." + sect + ".DATA");
                    int amount2 = HCF.getPlugin().getConfig().getInt("SHOP.SELL-MENU.ITEMS." + sect + ".AMOUNT");
                    String name = HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.ITEMS." + sect + ".NAME");
                    String material = HCF.getPlugin().getConfig().getString("SHOP.SELL-MENU.ITEMS." + sect + ".MATERIAL");
                    List<String> lore = HCF.getPlugin().getConfig().getStringList("SHOP.SELL-MENU.ITEMS." + sect + ".LORE");
                    ItemStack stack = new ItemMaker(Material.valueOf(material)).setData(data).setLore(CC.list(lore)).setName(CC.chat(name)).setAmount(amount2).build();
                    sellinv.setItem(slot, stack);
                }
                p.openInventory(sellinv);
            }
            if (e.getRawSlot() == HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SPAWNER-SHOP.SLOT")) {
                /* */
                Inventory spawnerinv;
                int size2 = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.SIZE");
                spawnerinv = Bukkit.createInventory(null, size2, ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.INVENTORY-NAME")));
                int glassdata2 = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.GLASS-COLOR");
                ItemStack glasspane2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) glassdata2);
                ItemMeta glassmeta2 = glasspane2.getItemMeta();
                glassmeta2.addEnchant(Enchantment.DURABILITY, 1, true);
                glassmeta2.setDisplayName(" ");
                glasspane2.setItemMeta(glassmeta2);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.SPAWNER-MENU.VIPER-GLASS")) {
                    spawnerinv.setItem(34, glasspane2);
                    spawnerinv.setItem(33, glasspane2);
                    spawnerinv.setItem(32, glasspane2);
                    spawnerinv.setItem(31, glasspane2);
                    spawnerinv.setItem(30, glasspane2);
                    spawnerinv.setItem(29, glasspane2);
                    spawnerinv.setItem(28, glasspane2);
                }
                /* */
                Material backmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.BACK-BUTTON.MATERIAL"));
                int backdata = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.BACK-BUTTON.DATA");
                ItemStack backbutton = new ItemStack(backmaterial, 1, (short) backdata);
                ItemMeta backmeta = backbutton.getItemMeta();
                List<String> backlore = new ArrayList<String>();
                backmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.BACK-BUTTON.NAME")));
                for (String lore : HCF.getPlugin().getConfig().getStringList("SHOP.SPAWNER-MENU.BACK-BUTTON.LORE")) {
                    backlore.add(ChatColor.translateAlternateColorCodes('&', lore));
                }
                backmeta.setLore(backlore);
                backbutton.setItemMeta(backmeta);
                if (HCF.getPlugin().getConfig().getBoolean("SHOP.SPAWNER-MENU.BACK-BUTTON.ENABLED")) {
                    spawnerinv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.BACK-BUTTON.SLOT"), backbutton);
                }
                for (String sect : HCF.getPlugin().getConfig().getConfigurationSection("SHOP.SPAWNER-MENU.ITEMS").getKeys(false)) {
                    int slot = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".SLOT");
                    int data = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".DATA");
                    int amount2 = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".AMOUNT");
                    String name = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".NAME");
                    String material = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".MATERIAL");
                    List<String> lore = HCF.getPlugin().getConfig().getStringList("SHOP.SPAWNER-MENU.ITEMS." + sect + ".LORE");
                    ItemStack stack = new ItemMaker(Material.valueOf(material)).setData(data).setLore(CC.list(lore)).setName(CC.chat(name)).setAmount(amount2).build();
                    spawnerinv.setItem(slot, stack);
                }
                p.openInventory(spawnerinv);
            }
        }

        /*
         HANDLE SHOP
         */

        if (!e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.INVENTORY-NAME"))) {
            return;
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.INVENTORY-NAME"))) {
            for (String sect : HCF.getPlugin().getConfig().getConfigurationSection("SHOP.BUY-MENU.ITEMS").getKeys(false)) {
                int price = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".PRICE");
                int data = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".DATA");
                int amount2 = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".AMOUNT");
                String name = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".NAME");
                String material = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".MATERIAL");
                List<String> lore = HCF.getPlugin().getConfig().getStringList("SHOP.BUY-MENU.ITEMS." + sect + ".LORE");
                ItemStack stack = new ItemMaker(Material.valueOf(material)).setData(data).setLore(CC.list(lore)).setName(CC.chat(name)).setAmount(amount2).build();
                String material2 = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.MATERIAL");
                int data2 = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.DATA");
                int amount = HCF.getPlugin().getConfig().getInt("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.AMOUNT");
                int playerBal = HCF.getPlugin().getBalanceManager().getBalance(p.getUniqueId());
                if (e.getCurrentItem() == null)
                    return;
                if (stack.isSimilar(e.getCurrentItem())) {
                    e.setCancelled(true);
                    if (playerBal < price) {
                        for (String msg : HCF.getPlugin().getConfig().getStringList("SHOP.INSUFFICIENT-FUNDS"))
                            p.sendMessage(CC.chat(msg.replaceAll("%price%", String.valueOf(price))));
                        p.closeInventory();
                        return;
                    }
                    if (getFreeSlots(p) < 1) {
                        for (String msg : HCF.getPlugin().getConfig().getStringList("SHOP.FULL-INVENTORY"))
                            p.sendMessage(CC.chat(msg));
                        return;
                    }
                    boolean isloreenabled = HCF.getPlugin().getConfig().getBoolean("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.MATCH-ITEM-LORE");
                    if (isloreenabled) {
                        String matchname = HCF.getPlugin().getConfig().getString("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.MATCH-NAME");
                        List<String> matchlore = HCF.getPlugin().getConfig().getStringList("SHOP.BUY-MENU.ITEMS." + sect + ".ITEM.MATCH-LORE");
                        ItemStack stack1 = new ItemMaker(Material.valueOf(material2)).setData(data2).setLore(CC.list(matchlore)).setName(CC.chat(matchname)).setAmount(amount).build();
                        p.getInventory().addItem(stack1);
                    } else {
                        p.getInventory().addItem(new ItemStack(Material.valueOf(material2), amount, (byte) data2));
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.BOUGHT-ITEM").replaceAll("%item%", material2)).replaceAll("%amount%", String.valueOf(amount)).replaceAll("%price%", String.valueOf(price)));
                    HCF.getPlugin().getBalanceManager().takeBalance(p, price);
                    p.updateInventory();
                }
            }
        }
    }

    public int getFreeSlots(Player player) {
        int slots = 0;
        ItemStack[] contents;
        for (int length = (contents = player.getInventory().getContents()).length, i = 0; i < length; ++i) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() == Material.AIR) {
                ++slots;
            }
        }
        return slots;
    }

}
