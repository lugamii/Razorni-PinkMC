package dev.razorni.crates.crate.menu.editor.buttons;


import dev.razorni.crates.crate.Crate;
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
public class CrateShowPercentageButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .setDisplayName(ChatColor.YELLOW + "Percentage")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to change whether the crate should show percentages on rewards.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        boolean showPercentages = !crate.isShowPercentage();
        crate.setShowPercentage(showPercentages);

        player.sendMessage(crate.getDisplayName() + ChatColor.GREEN + " "
                + (crate.isShowPercentage() ? "will now" : "will no longer") + " show percentages.");
    }
}
