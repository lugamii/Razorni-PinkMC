package dev.razorni.crates.crate.menu.editor.buttons;


import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ItemNbtUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@RequiredArgsConstructor
public class CrateEditItemsButton extends Button {

    private final Crate crate;
    private final Crates plugin;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.CHEST)
                .setDisplayName(ChatColor.YELLOW + "Edit Items")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to edit crate items.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.closeInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCrateManager()
                        .getCrateEditingMap().put(player.getUniqueId(), crate);

                Inventory inventory = Bukkit.createInventory(null, 54, crate.getDisplayName());

                crate.getItems().forEach(crateItem -> {
                    ItemNbtUtil.set(crateItem.getItemStack(), "uuid", crateItem.getUuid().toString());
                    inventory.setItem(crateItem.getSlot(), crateItem.getItemStack());
                });


                player.openInventory(inventory);
            }
        }.runTaskLater(JavaPlugin.getPlugin(Crates.class), 5L);
    }
}
