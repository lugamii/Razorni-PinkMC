package dev.razorni.hcfactions.extras.ability.menu;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AbilityListMenu extends Menu {
    public AbilityListMenu(MenuManager manager, Player player) {
        super(manager, player, "Abilities", 54, false);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int slot = 1;
        for (Ability ability : this.getInstance().getAbilityManager().getAbilities().values()) {
            buttons.put(slot, new Button() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }

                @Override
                public ItemStack getItemStack() {
                    return ability.getItem();
                }
            });
            ++slot;
        }
        return buttons;
    }
}
