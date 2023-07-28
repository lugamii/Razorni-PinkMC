package dev.razorni.crates.crate.listener;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import dev.razorni.crates.crate.CrateItem;
import dev.razorni.crates.crate.menu.CratePreviewMenu;
import cc.invictusgames.ilib.placeholder.PlaceholderService;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ItemNbtUtil;
import cc.invictusgames.ilib.utils.ItemUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CrateListener implements Listener {

    private final Crates plugin;


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null || !player.isOp())
            return;

        Block blockPlaced = event.getBlockPlaced();
        Crate crate = plugin.getCrateManager().isBuiltCrate(itemInHand);

        if (crate == null)
            return;

        crate.setLocation(blockPlaced.getLocation());
        plugin.getCrateManager().saveCrate(crate, true);

        blockPlaced.setMetadata("crate", new FixedMetadataValue(plugin, crate.getName()));

        event.getPlayer().sendMessage(CC.GREEN + "You set the location of "
                + crate.getDisplayName() + CC.GREEN + ".");

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCrateManager().loadCrate(crate);
            }
        }.runTaskLater(plugin, 10L);
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Crate crate = plugin.getCrateManager().getCrateEditingMap().get(player.getUniqueId());

        if (!plugin.getCrateManager().getCrateEditingMap().containsKey(player.getUniqueId())
                || crate == null)
            return;

        List<CrateItem> newItems = new ArrayList<>();

        for (int i = 0; i < event.getInventory().getSize(); i++) {
            ItemStack itemStack = event.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            String uuid = ItemNbtUtil.getString(itemStack, "uuid");

            if (uuid == null || uuid.isEmpty()) {
                newItems.add(new CrateItem(itemStack, -1, i));
                continue;
            }

            CrateItem item = crate.getItem(UUID.fromString(uuid));
            if (item == null) {
                newItems.add(new CrateItem(itemStack, -1, i));
                continue;
            }

            item.setItemStack(itemStack);
            item.setSlot(i);
            newItems.add(item);
        }

        crate.getItems().clear();
        crate.getItems().addAll(newItems);

        player.sendMessage(ChatColor.GREEN + "Finished editing " + crate.getDisplayName()
                + ChatColor.GREEN + ".");

        plugin.getCrateManager().saveCrate(crate, true);
        plugin.getCrateManager().getCrateEditingMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPreview(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK
                && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block clickedBlock = event.getClickedBlock();
        Crate crate = plugin.getCrateManager().getCrate(clickedBlock.getLocation());

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && crate != null) {
            if (player.isOp() && player.isSneaking()
                    && player.getGameMode() == GameMode.CREATIVE) {

                player.sendMessage(ChatColor.YELLOW + "You removed the location of "
                        + crate.getDisplayName() + ChatColor.YELLOW + ".");
                crate.deleteHologram();
                crate.setLocation(null);
                clickedBlock.removeMetadata("crate", plugin);
                return;
            }

            new CratePreviewMenu(crate).openMenu(player);
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (crate != null && !crate.getKey().isSimilar(player.getItemInHand())) {
                player.sendMessage(ChatColor.RED + "You do not have a "
                        + crate.getDisplayName() + ChatColor.RED + " key.");
                event.setCancelled(true);
                return;
            }

            Crate crateFromHand = plugin.getCrateManager().getCrate(player.getItemInHand());
            if (crateFromHand != null
                    && !crateFromHand.getLocation().equals(clickedBlock.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can not place a crate key.");
                event.setCancelled(true);
                return;
            }

            if (crate == null)
                return;

            ItemStack key = crate.getKey().clone();
            key.setAmount(1);
            player.getInventory().removeItem(key);

            List<CrateItem> winnings = new ArrayList<>();
            for (int i = 0; i < crate.getRewardAmount(); i++) {
                CrateItem win = crate.getRandomReward();

                if (win == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't get a random winning.");
                    break;
                }

                ItemStack clone = win.getItemStack().clone();
                ItemNbtUtil.remove(clone, "uuid");

                if (!win.getCommands().isEmpty())
                    win.getCommands().forEach(command -> Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            PlaceholderService.replace(player, command)
                    ));
                else player.getInventory().addItem(clone).values()
                        .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

                winnings.add(win);
            }

            if (crate.isBroadcast() && !winnings.isEmpty())
                Bukkit.broadcastMessage(
                        CC.format("&6[KingOfTheHill] &6%s &ehas obtained %s&e, from a &9Event Key&e.", player.getName(),
                                winnings.stream().map(CrateItem::getItemStack).map(is ->
                                                (ChatColor.BLUE + (is.getAmount() == 0 ? "x1" : "x" + is.getAmount())) + " "
                                                        + ChatColor.RESET + ItemUtils.getName(is))
                                        .collect(Collectors.joining(ChatColor.YELLOW + ", " + ChatColor.RESET))));

            player.sendMessage(ChatColor.GREEN + "You have opened a "
                    + crate.getDisplayName() + ChatColor.GREEN + " crate.");

            event.setCancelled(true);
        }
    }

}
