package dev.razorni.crates.lootbox.listener;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
import dev.razorni.crates.lootbox.LootBoxItem;
import dev.razorni.crates.lootbox.menu.LootBoxMenu;
import dev.razorni.crates.lootbox.profile.LootBoxProfile;
import cc.invictusgames.ilib.utils.ItemNbtUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class LootBoxListener implements Listener {

    private final Crates plugin;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        LootBox lootBox = null;
        if (plugin.getLootBoxManager().getLootBoxEditingMap().containsKey(player.getUniqueId()))
            lootBox = plugin.getLootBoxManager().getLootBoxEditingMap().get(player.getUniqueId());
        else if (plugin.getLootBoxManager().getLootBoxFinalEditingMap().containsKey(player.getUniqueId()))
            lootBox = plugin.getLootBoxManager().getLootBoxFinalEditingMap().get(player.getUniqueId());

        if (lootBox == null)
            return;

        List<LootBoxItem> newItems = new ArrayList<>();

        for (int i = 0; i < event.getInventory().getSize(); i++) {
            ItemStack itemStack = event.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            String uuid = ItemNbtUtil.getString(itemStack, "uuid");

            if (uuid == null || uuid.isEmpty()) {
                newItems.add(new LootBoxItem(itemStack, -1));
                continue;
            }

            LootBoxItem item = lootBox.getItem(UUID.fromString(uuid));
            if (item == null) {
                newItems.add(new LootBoxItem(itemStack, -1));
                continue;
            }

            item.setItemStack(itemStack);
            newItems.add(item);
        }

        if (plugin.getLootBoxManager().getLootBoxEditingMap().containsKey(player.getUniqueId())) {
            lootBox.getItems().clear();
            lootBox.getItems().addAll(newItems);
        } else if (plugin.getLootBoxManager().getLootBoxFinalEditingMap().containsKey(player.getUniqueId())) {
            lootBox.getFinalItems().clear();
            lootBox.getFinalItems().addAll(newItems);
        }

        player.sendMessage(ChatColor.GREEN + "Finished editing " + lootBox.getDisplayName()
                + ChatColor.GREEN + ".");

        plugin.getLootBoxManager().saveLootBox(lootBox, true);
        plugin.getLootBoxManager().getLootBoxEditingMap().remove(player.getUniqueId());
        plugin.getLootBoxManager().getLootBoxFinalEditingMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getItem() == null || event.getItem().getType() == Material.AIR)
            return;

        LootBox lootBox = plugin.getLootBoxManager().getLootBox(player.getItemInHand());
        if (lootBox == null)
            return;

        LootBoxProfile lootBoxProfile = LootBoxProfile.getLootBoxProfile(player.getUniqueId());
        if (lootBoxProfile.isOpening())
            return;

        lootBoxProfile.setOpening(true);
        new LootBoxMenu(lootBox, player, plugin).openMenu(player);
        if (player.getItemInHand().getAmount() > 1)
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        else player.setItemInHand(new ItemStack(Material.AIR));
    }
}
