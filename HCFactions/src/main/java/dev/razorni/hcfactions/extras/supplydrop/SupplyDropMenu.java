package dev.razorni.hcfactions.extras.supplydrop;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created By LeandroSSJ
 * Created on 26/06/2021
 */
public class SupplyDropMenu implements Listener {

    public static HCF onOpenEditorInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 5 * 9, CC.translate("&bAirdrop Loot &7(Edit)"));

        Iterator contentsGkit = HCF.getPlugin().getSupplyDropManager().getData().getConfigurationSection(("supplydrop")).getKeys(false).iterator();
        int n = 0;
        while (contentsGkit.hasNext()) {
            String items = (String) contentsGkit.next();
            ++n;
            ItemStack itemStack = HCF.getPlugin().getSupplyDropManager().getData().getItemStack("supplydrop." + items + ".item");
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                inv.setItem(n - 1, itemStack);
            }
        }


        player.openInventory(inv);
        return HCF.getPlugin();
    }


    @EventHandler
    public HCF onInvClosed(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase(CC.translate("&bAirdrop Loot &7(Edit)"))) {
            Inventory inventory = e.getInventory();
            int n = 0;
            for (int items = 0; items < 5 * 9; ++items) {
                ItemStack itemStack = inventory.getItem(items);
                ++n;

                FileConfiguration kit = HCF.getPlugin().getSupplyDropManager().getData();
                kit.set("airdrop." + n + ".item", itemStack);

                try {
                    kit.save(HCF.getPlugin().getSupplyDropManager().getFile());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
        return HCF.getPlugin();
    }
}
