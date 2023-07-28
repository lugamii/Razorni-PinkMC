package dev.razorni.crates.crate.menu.editor.buttons;


import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.menu.TextEditMenu;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@RequiredArgsConstructor
public class CrateHologramButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.BEACON)
                .setDisplayName(ChatColor.YELLOW + "Hologram Editor")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Clcik to edit the hologram of the crate.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        new TextEditMenu(
                crate.getHologramLines(),
                strings -> {
                    crate.setHologramLines(strings);
                    crate.updateHologram();
                },
                "Editing " + crate.getDisplayName()
        ).openMenu(player);
    }
}
