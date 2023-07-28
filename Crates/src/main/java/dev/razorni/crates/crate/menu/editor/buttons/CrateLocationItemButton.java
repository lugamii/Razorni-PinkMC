package dev.razorni.crates.crate.menu.editor.buttons;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.chatinput.ChatInput;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@RequiredArgsConstructor
public class CrateLocationItemButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(crate.getLocationItem())
                .setDisplayName(ChatColor.YELLOW + "Crate Location Item")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to change the location item of the crate.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player whoClicked, int slot, ClickType clickType, int hotbarButton) {
        whoClicked.closeInventory();

        new ChatInput<ItemStack>(ItemStack.class)
                .text(ChatColor.GREEN + "Insert a new location item.")
                .accept((player, itemStack) -> {

                    crate.setLocationItem(itemStack.getType());

                    player.sendMessage(ChatColor.GREEN + "You set the location item to "
                            + crate.getLocationItem().name() + ChatColor.GREEN + ".");

                    Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(Crates.class), () -> {
                        crate.getLocation().getBlock().setType(crate.getLocationItem());
                    });
                    return true;
                }).send(whoClicked);
    }
}
