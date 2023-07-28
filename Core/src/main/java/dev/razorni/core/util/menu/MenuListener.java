package dev.razorni.core.util.menu;

import dev.razorni.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class MenuListener implements Listener {

    private Core instance;

    public MenuListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Menu.currentlyOpenedMenus.remove(event.getPlayer().getName());
        if (Menu.checkTasks.containsKey(event.getPlayer().getName())) {
            Menu.checkTasks.get(event.getPlayer().getName()).cancel();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu menu = Menu.currentlyOpenedMenus.get(player.getName());

        if (menu == null) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getType() != InventoryType.CHEST) {
            return;
        }

        event.setCancelled(true);
        int slot = event.getSlot();
        Map<Integer, Button> buttons = menu.getButtons(player);

        if (buttons.containsKey(slot)) {
            Button button = buttons.get(slot);
            button.clicked(player, event.getClick());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu menu = Menu.currentlyOpenedMenus.get(player.getName());

        if (menu == null) {
            return;
        }

        menu.onClose(player);
        Menu.currentlyOpenedMenus.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Menu menu = Menu.currentlyOpenedMenus.get(player.getName());

        if (menu == null) {
            return;
        }

        menu.onClose(player);
        Menu.currentlyOpenedMenus.remove(player.getName());
    }

}
