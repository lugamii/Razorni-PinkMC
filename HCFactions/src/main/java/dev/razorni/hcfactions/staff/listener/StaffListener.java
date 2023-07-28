package dev.razorni.hcfactions.staff.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.staff.StaffManager;
import dev.razorni.hcfactions.staff.extra.StaffItem;
import dev.razorni.hcfactions.staff.extra.StaffItemAction;
import dev.razorni.hcfactions.staff.menu.InspectionMenu;
import dev.razorni.hcfactions.staff.menu.SilentViewMenu;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class StaffListener extends Module<StaffManager> {
    private final List<String> disabledFrozenCommands;
    private final Cooldown interactCooldown;

    public StaffListener(StaffManager manager) {
        super(manager);
        this.interactCooldown = new Cooldown(manager);
        this.disabledFrozenCommands = this.getConfig().getStringList("STAFF_MODE.DISABLED_COMMANDS_FROZEN");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isFrozen(player)) {
            for (String s : this.disabledFrozenCommands) {
                if (event.getMessage().contains(s)) {
                    continue;
                }
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.NOT_ALLOWED_COMMAND"));
                break;
            }
        }
    }

    @EventHandler
    public void onInspect(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        if (this.getManager().isStaffEnabled(player)) {
            ItemStack stack = this.getManager().getItemInHand(player);
            if (stack == null) {
                return;
            }
            StaffItem item = this.getManager().getStaffItems().get(stack);
            if (item == null) {
                return;
            }
            if (item.getAction() == null) {
                return;
            }
            if (item.getAction() == StaffItemAction.INSPECTION) {
                new InspectionMenu(this.getInstance().getMenuManager(), player, target).open();
            } else if (item.getAction() == StaffItemAction.FREEZE) {
                player.chat("/freeze " + target.getName());
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.setCancelled(true);
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onInspect(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (!this.getManager().isStaffEnabled(player) && !this.getManager().isVanished(player)) {
            return;
        }
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            new SilentViewMenu(this.getInstance().getMenuManager(), player, chest.getInventory()).open();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlate(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Player player = event.getPlayer();
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }

    private void handleClick(Player player) {
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack != null) {
            StaffItem item = this.getManager().getStaffItems().get(stack);
            if (item == null) {
                return;
            }
            if (item.getAction() == null) {
                return;
            }
            if (item.getReplacement() != null) {
                for (StaffItem staffItem : this.getManager().getStaffItems().values()) {
                    if (staffItem.getAction() == null) {
                        continue;
                    }
                    if (!staffItem.getAction().name().equals(item.getReplacement())) {
                        continue;
                    }
                    this.getManager().setItemInHand(player, staffItem.getItem());
                }
            }
            switch (item.getAction()) {
                case RANDOM_TP: {
                    player.chat("/randomtp");
                    break;
                }
                case VANISH_OFF: {
                    this.getManager().disableVanish(player);
                    break;
                }
                case VANISH_ON: {
                    this.getManager().enableVanish(player);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isStaffEnabled(player)) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        if (this.interactCooldown.hasCooldown(player)) {
            return;
        }
        ItemStack stack = this.getManager().getItemInHand(player);
        if (this.getManager().isStaffEnabled(player)) {
            if (stack.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Staff List")) {
                player.chat("/liststaff");
            }
            this.interactCooldown.applyCooldownTicks(player, 100);
            this.handleClick(player);
            if (!this.getManager().isStaffBuild(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageFrozen(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.getManager().isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onClickFrozen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!this.getManager().isStaffBuild(player) && this.getManager().isStaffEnabled(player)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.DENY_PLACE"));
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isStaffEnabled(player)) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onDamageStaff(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupFrozen(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuitFrozen(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isFrozen(player)) {
            this.getManager().unfreezePlayer(player);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!this.getManager().isStaffBuild(player) && this.getManager().isStaffEnabled(player)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.DENY_BREAK"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("azurite.staff")) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onDropFrozen(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!this.getManager().isStaffBuild(player) && this.getManager().isStaffEnabled(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().isStaffBuild(player)) {
            return;
        }
        if (this.getManager().isStaffEnabled(player) || this.getManager().isVanished(player)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.DENY_DROP"));
        }
    }
}