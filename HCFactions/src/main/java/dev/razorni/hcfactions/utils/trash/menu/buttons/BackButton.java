package dev.razorni.hcfactions.utils.trash.menu.buttons;

import dev.razorni.hcfactions.utils.trash.item.ItemBuilder;
import dev.razorni.hcfactions.utils.trash.menu.Button;
import dev.razorni.hcfactions.utils.trash.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackButton extends Button {

    private final Menu back;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.REDSTONE)
                .name("&c&lBack")
                .lore(
                        "&ChatUtillick here to return",
                        "&cto the previous menu."
                )
                .build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }
}
