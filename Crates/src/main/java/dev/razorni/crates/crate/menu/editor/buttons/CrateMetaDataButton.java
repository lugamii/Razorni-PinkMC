package dev.razorni.crates.crate.menu.editor.buttons;

import dev.razorni.crates.crate.Crate;
import dev.razorni.crates.crate.menu.editor.CrateMetaDataMenu;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@RequiredArgsConstructor
public class CrateMetaDataButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.COMMAND)
                .setDisplayName(ChatColor.YELLOW + "Crate Meta-Data")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to edit crate meta-data.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        new CrateMetaDataMenu(crate).openMenu(player);
    }
}
