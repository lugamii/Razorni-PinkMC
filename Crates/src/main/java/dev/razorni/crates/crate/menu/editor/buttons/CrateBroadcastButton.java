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
public class CrateBroadcastButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.NOTE_BLOCK)
                .setDisplayName(ChatColor.YELLOW + "Broadcast")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to change whether the crate should broadcast rewards.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        boolean broadcast = !crate.isBroadcast();
        crate.setBroadcast(broadcast);

        player.sendMessage(crate.getDisplayName() + ChatColor.GREEN + " "
                + (crate.isBroadcast() ? "will now" : "will no longer") + " broadcast.");
    }
}
