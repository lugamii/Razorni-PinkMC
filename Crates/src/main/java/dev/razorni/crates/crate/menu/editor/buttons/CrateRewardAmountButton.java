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
public class CrateRewardAmountButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.DIAMOND)
                .setDisplayName(ChatColor.YELLOW + "Current reward amount: " + ChatColor.RED + crate.getRewardAmount())
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to change the reward amount of the crate.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (clickType.isLeftClick())
            crate.setRewardAmount(crate.getRewardAmount() + 1);

        if (clickType.isRightClick()) {
            if (crate.getRewardAmount() > 1)
                crate.setRewardAmount(crate.getRewardAmount() - 1);
        }
    }
}
