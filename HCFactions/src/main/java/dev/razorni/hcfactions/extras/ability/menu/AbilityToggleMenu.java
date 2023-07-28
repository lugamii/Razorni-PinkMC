package dev.razorni.hcfactions.extras.ability.menu;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import dev.razorni.hcfactions.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AbilityToggleMenu extends Menu {
    public AbilityToggleMenu(MenuManager manager, Player player) {
        super(manager, player, "Ability Toggles", 54, false);
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
                    ability.setEnabled(!ability.isEnabled());
                    AbilityToggleMenu.this.getAbilitiesConfig().set(String.valueOf(new StringBuilder().append(ability.getNameConfig()).append(".ENABLED")), ability.isEnabled());
                    AbilityToggleMenu.this.getAbilitiesConfig().save();
                    AbilityToggleMenu.this.update();
                }

                @Override
                public ItemStack getItemStack() {
                    ItemBuilder builder = new ItemBuilder(ability.getItem().clone());
                    builder.setLore(String.valueOf(new StringBuilder().append("&eEnabled: ").append(ability.isEnabled() ? "&atrue" : "&cfalse")));
                    return builder.toItemStack();
                }
            });
            ++slot;
        }
        return buttons;
    }
}
