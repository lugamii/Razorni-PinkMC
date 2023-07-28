package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class BorderListener extends Module<ListenerManager> {
    private final int netherBorder;
    private final int worldBorder;

    public BorderListener(ListenerManager manager) {
        super(manager);
        this.worldBorder = this.getConfig().getInt("BORDERS.WORLD");
        this.netherBorder = this.getConfig().getInt("BORDERS.NETHER");
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Location location = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_INTERACT"));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_BREAK"));
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (!this.inBorder(entity.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Location location = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_PLACE"));
        }
    }

    public boolean inBorder(Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        if (location.getWorld().getEnvironment() == World.Environment.NORMAL) {
            return Math.abs(x) <= this.worldBorder && Math.abs(z) <= this.worldBorder;
        }
        return location.getWorld().getEnvironment() != World.Environment.NETHER || (Math.abs(x) <= this.netherBorder && Math.abs(z) <= this.netherBorder);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location location = event.getBlockPlaced().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_PLACE"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_INTERACT"));
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Location location = event.getTo();
        if (this.inBorder(location)) {
            return;
        }
        if (location.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        int x = location.getBlockX();
        int z = location.getBlockZ();
        boolean b = false;
        if (Math.abs(x) > this.worldBorder) {
            location.setX((x > 0) ? ((double) (this.worldBorder - 50)) : ((double) (-this.worldBorder + 50)));
            b = true;
        }
        if (Math.abs(z) > this.worldBorder) {
            location.setZ((z > 0) ? ((double) (this.worldBorder - 50)) : ((double) (-this.worldBorder + 50)));
            b = true;
        }
        if (b) {
            location.add(0.5, 0.0, 0.5);
            event.setTo(location);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!this.inBorder(entity.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (!this.inBorder(event.getTo())) {
            event.setTo(event.getFrom());
            player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_WALK"));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location location = event.getTo();
        Player player = event.getPlayer();
        if (this.inBorder(location)) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        event.setCancelled(true);
        this.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        player.sendMessage(this.getLanguageConfig().getString("BORDER_LISTENER.CANNOT_TELEPORT"));
    }
}
