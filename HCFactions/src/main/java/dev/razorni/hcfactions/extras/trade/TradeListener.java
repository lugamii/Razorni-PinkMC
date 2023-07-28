package dev.razorni.hcfactions.extras.trade;

import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            if (openMenu instanceof TradeMenu) {
                TradeMenu tradeMenu = (TradeMenu) openMenu;

                if (event.getClickedInventory() == null) {
                    return;
                }

                int slot = event.getSlot();

                ItemStack clickedItem = event.getClickedInventory().getItem(slot);

                if (clickedItem == null) {
                    return;
                }

                Inventory inventory = event.getClickedInventory();

                if (inventory != player.getInventory()) {
                    return;
                }

                if (tradeMenu.getItemsByPlayer(player).size() >= 20) {
                    return;
                }

                event.setCurrentItem(null);
                inventory.setItem(slot, null);
                tradeMenu.getItemsByPlayer(player).add(clickedItem.clone());
            }
        }
    }

}