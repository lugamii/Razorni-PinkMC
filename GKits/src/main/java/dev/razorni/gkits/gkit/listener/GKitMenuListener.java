package dev.razorni.gkits.gkit.listener;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.gkit.menu.GKitMenuItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GKitMenuListener implements Listener {

    private final GKits plugin;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!plugin.getGKitManager().getIsEditing().contains(player.getUniqueId()))
            return;

        List<GKitMenuItem> newItems = new ArrayList<>();

        for (int i = 0; i < event.getInventory().getSize(); i++) {
            ItemStack itemStack = event.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            GKitMenuItem gKitMenuItem = new GKitMenuItem();
            gKitMenuItem.setItemStack(itemStack);
            gKitMenuItem.setSlot(i);

            if (newItems.contains(gKitMenuItem))
                continue;

            newItems.add(gKitMenuItem);
        }

        plugin.getGKitMenuConfig().getKitMenuItemList().clear();
        plugin.getGKitMenuConfig().getKitMenuItemList().addAll(newItems);

        plugin.getGKitMenuConfig().saveConfig();
        plugin.getGKitManager().getIsEditing().remove(player.getUniqueId());
    }
}
