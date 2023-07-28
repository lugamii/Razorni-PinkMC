package dev.razorni.crates.lootbox.menu.editor.buttons;


import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
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
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class LootBoxEditFinalItemsButton extends Button {

    private final LootBox lootBox;
    private final Crates plugin;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.ENDER_CHEST)
                .setDisplayName(ChatColor.YELLOW + "Edit Final Items")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to edit final lootbox items.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.closeInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getLootBoxManager()
                        .getLootBoxFinalEditingMap().put(player.getUniqueId(), lootBox);

                Inventory inventory = Bukkit.createInventory(null, 54, lootBox.getDisplayName());

                AtomicInteger slot = new AtomicInteger();
                lootBox.getFinalItems().forEach(lootBoxItem -> {
                    ItemNbtUtil.set(lootBoxItem.getItemStack(), "uuid", lootBoxItem.getUuid().toString());
                    inventory.setItem(slot.getAndIncrement(), lootBoxItem.getItemStack());
                });


                player.openInventory(inventory);
            }
        }.runTaskLater(JavaPlugin.getPlugin(Crates.class), 5L);
    }
}
