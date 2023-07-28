package dev.razorni.crates.lootbox.menu.editor.buttons;

import dev.razorni.crates.lootbox.LootBox;
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
public class LootBoxFinalMetaDataButton extends Button {

    private final LootBox lootBox;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.COMMAND_MINECART)
                .setDisplayName(ChatColor.YELLOW + "LootBox Final Meta-Data")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to edit lootbox final meta-data.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        new LootBoxFinalMetaDataMenu(lootBox).openMenu(player);
    }
}
