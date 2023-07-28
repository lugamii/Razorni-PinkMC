package dev.razorni.hcfactions.utils.glass;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class GlassManager implements Listener {

    private final Table<UUID, Location, GlassInfo> glassCache;
    private final ReentrantLock lock;
    private final Set<Material> overriddenBlocks;
    private ScheduledThreadPoolExecutor executor;

    public GlassManager() {
        this.glassCache = HashBasedTable.create();
        this.lock = new ReentrantLock();

        this.overriddenBlocks = EnumSet.of(Material.AIR, Material.LONG_GRASS, Material.DOUBLE_PLANT, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.VINE);

        Tasks.runLater(HCF.getPlugin(), this::setupTasks, 10L);
        Bukkit.getPluginManager().registerEvents(this, HCF.getPlugin());
    }

    private void setupTasks() {
        this.executor = new ScheduledThreadPoolExecutor(2, Tasks.newThreadFactory("Glass Thread - %d"));
        this.executor.setRemoveOnCancelPolicy(true);
    }

    public void disable() {
        this.glassCache.clear();

        if (this.executor != null) this.executor.shutdownNow();
    }

    public GlassInfo getGlassAt(Player player, Location location) {
        return this.glassCache.get(player.getUniqueId(), location);
    }

    public void generateGlassVisual(Player player, GlassInfo info) {
        if (this.glassCache.contains(player.getUniqueId(), info.getLocation())) return;

        int x = info.getLocation().getBlockX() >> 4;
        int z = info.getLocation().getBlockZ() >> 4;

        if (!info.getLocation().getWorld().isChunkLoaded(x, z)) return;

        info.getLocation().getWorld().getChunkAtAsync(x, z, (chunk) -> {
            Material material = info.getLocation().getBlock().getType();
            if (!this.overriddenBlocks.contains(material)) return;

            player.sendBlockChange(info.getLocation(), info.getMaterial(), info.getData());

            this.lock.lock();

            try {
                this.glassCache.put(player.getUniqueId(), info.getLocation(), info);
            } finally {
                this.lock.unlock();
            }
        });
    }

    public void generatePillar(Player player, GlassInfo info) {
        for (int i = 0; i < player.getLocation().getBlockY() + 60; i++) {
            Location loc = info.getLocation().clone();
            loc.setY(i);
            HCF.getPlugin().getGlassManager().generateGlassVisual(player, new GlassInfo(GlassManager.GlassType.CLAIM_MAP, loc, i % 5 == 0 ? info.getMaterial() : Material.GLASS, (byte) 0));
        }
    }

    public void clearGlassVisuals(Player player, GlassType type) {
        this.clearGlassVisuals(player, glassInfo -> glassInfo.getType() == type);
    }

    public void clearGlassVisuals(Player player, GlassType type, Predicate<GlassInfo> predicate) {
        this.clearGlassVisuals(player, glassInfo -> glassInfo.getType() == type && predicate.test(glassInfo));
    }

    private void clearGlassVisuals(Player player, Predicate<GlassInfo> predicate) {
        this.lock.lock();

        try {
            Iterator<Entry<Location, GlassInfo>> iterator = this.glassCache.row(player.getUniqueId()).entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<Location, GlassInfo> entry = iterator.next();
                if (!predicate.test(entry.getValue())) continue;

                Location location = entry.getKey();

                if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                    iterator.remove();
                    continue;
                }

                player.sendBlockChange(entry.getKey(), location.getBlock().getType(), location.getBlock().getData());
                iterator.remove();
            }
        } finally {
            this.lock.unlock();
        }
    }

    private GlassType getGlassType(Player player, GlassType forced) {
        if (forced != null) {
            return forced;
        } else {
            return null;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.lock.lock();

        try {
            this.glassCache.row(player.getUniqueId()).clear();
        } finally {
            this.lock.unlock();
        }
    }

    public enum GlassType {
        SPAWN_WALL, CLAIM_WALL, CLAIM_MAP, CLAIM_SELECTION
    }
}