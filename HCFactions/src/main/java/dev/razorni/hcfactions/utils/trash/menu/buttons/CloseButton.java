package dev.razorni.hcfactions.utils.trash.menu.buttons;

import dev.razorni.hcfactions.utils.trash.item.ItemBuilder;
import dev.razorni.hcfactions.utils.trash.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.INK_SACK)
                .data(1)
                .name("&ChatUtillose")
                .build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        playNeutral(player);
        player.closeInventory();
    }
}
