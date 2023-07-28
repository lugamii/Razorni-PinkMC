package dev.razorni.hcfactions.extras.shop.listener;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemMaker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpawnerShopListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.INVENTORY-NAME"))) {
            return;
        }
        if (e.getInventory().getTitle().equalsIgnoreCase(HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.INVENTORY-NAME"))) {
            for (String sect : HCF.getPlugin().getConfig().getConfigurationSection("SHOP.SPAWNER-MENU.ITEMS").getKeys(false)) {
                int price = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".PRICE");
                int data = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".DATA");
                int amount2 = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".AMOUNT");
                String name = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".NAME");
                String material = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".MATERIAL");
                List<String> lore = HCF.getPlugin().getConfig().getStringList("SHOP.SPAWNER-MENU.ITEMS." + sect + ".LORE");
                ItemStack stack = new ItemMaker(Material.valueOf(material)).setData(data).setLore(CC.list(lore)).setName(CC.chat(name)).setAmount(amount2).build();
                String material2 = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.MATERIAL");
                int data2 = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.DATA");
                int amount = HCF.getPlugin().getConfig().getInt("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.AMOUNT");
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
                    boolean isloreenabled = HCF.getPlugin().getConfig().getBoolean("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.MATCH-ITEM-LORE");
                    if (isloreenabled) {
                        String matchname = HCF.getPlugin().getConfig().getString("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.MATCH-NAME");
                        List<String> matchlore = HCF.getPlugin().getConfig().getStringList("SHOP.SPAWNER-MENU.ITEMS." + sect + ".ITEM.MATCH-LORE");
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
