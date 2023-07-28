package dev.razorni.hcfactions.extras.spawners.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.spawners.Spawner;
import dev.razorni.hcfactions.extras.spawners.SpawnerManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerListener extends Module<SpawnerManager> {
    public SpawnerListener(SpawnerManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.isCancelled()) {
            return;
        }
        if (!this.getInstance().getTeamManager().canBuild(player, block.getLocation())) {
            return;
        }
        if (block.getState() instanceof CreatureSpawner) {
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("SPAWNER_LISTENER.CANNOT_BREAK"));
                return;
            }
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            Spawner spawner = this.getManager().getSpawners().get(creatureSpawner.getSpawnedType());
            event.setCancelled(true);
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(block.getLocation(), spawner.getItemStack());
            player.sendMessage(this.getLanguageConfig().getString("SPAWNER_LISTENER.BREAK_SPAWNER").replaceAll("%type%", spawner.getName()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack stack = event.getItemInHand();
        if (event.isCancelled()) {
            return;
        }
        if (!this.getInstance().getTeamManager().canBuild(player, block.getLocation())) {
            return;
        }
        if (block.getState() instanceof CreatureSpawner && stack != null) {
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                if (!player.isOp()) {
                    event.setCancelled(true);
                    player.sendMessage(this.getLanguageConfig().getString("SPAWNER_LISTENER.CANNOT_PLACE"));
                    return;
                }
            }
            Spawner spawner = this.getManager().getByItem(stack);
            if (spawner == null) {
                return;
            }
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            creatureSpawner.setSpawnedType(spawner.getType());
            creatureSpawner.update();
            player.sendMessage(this.getLanguageConfig().getString("SPAWNER_LISTENER.PLACED_SPAWNER").replaceAll("%type%", spawner.getName()));
        }
    }
}