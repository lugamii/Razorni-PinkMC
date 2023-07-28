package dev.razorni.hcfactions.staff.menu;

import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SilentViewMenu extends Menu {
    private final Inventory viewing;

    public SilentViewMenu(MenuManager manager, Player player, Inventory viewing) {
        super(manager, player, manager.getConfig().getString("STAFF_MODE.SILENT_VIEW_TITLE"), Math.max(viewing.getSize(), 9), true);
        this.viewing = viewing;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] contents = this.viewing.getContents();
        for (int i = 0; i < contents.length; ++i) {
            ItemStack stack = contents[i];
            buttons.put(i + 1, new Button() {
                @Override
                public ItemStack getItemStack() {
                    return stack;
                }

                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            });
        }
        return buttons;
    }
}
