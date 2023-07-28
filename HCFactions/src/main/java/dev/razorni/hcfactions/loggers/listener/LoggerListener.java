package dev.razorni.hcfactions.loggers.listener;

import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.loggers.Logger;
import dev.razorni.hcfactions.loggers.LoggerManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoggerListener extends Module<LoggerManager> {
    public LoggerListener(LoggerManager manager) {
        super(manager);
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        Entity[] entities = event.getChunk().getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Villager) {
                Logger logger = this.getManager().getLoggers().remove(entity.getUniqueId());
                if (logger != null && !logger.getVillager().isDead()) {
                    logger.getVillager().remove();
                    logger.getRemoveTask().cancel();
                    this.getManager().getPlayers().remove(logger.getPlayer().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getManager().removeLogger(player);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager)) {
            return;
        }
        Logger logger = this.getManager().getLoggers().remove(entity.getUniqueId());
        if (logger == null) {
            return;
        }
        event.getDrops().clear();
        event.getDrops().addAll(Arrays.stream(logger.getContents()).filter(Objects::nonNull).collect(Collectors.toList()));
        event.getDrops().addAll(Arrays.stream(logger.getArmorContents()).filter(Objects::nonNull).collect(Collectors.toList()));
        event.setDroppedExp((int) logger.getExp());
        this.getInstance().getVersionManager().getVersion().handleLoggerDeath(logger);
        this.getInstance().getDeathbanManager().applyDeathban(logger.getPlayer());
        this.getInstance().getTeamManager().handleDeath(logger.getPlayer(), logger.getVillager().getKiller());
        this.getManager().getPlayers().remove(logger.getPlayer().getUniqueId());
        this.handleDeathMessage(logger);
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (!this.getManager().getLoggers().containsKey(entity.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (!this.getManager().getLoggers().containsKey(entity.getUniqueId())) {
            return;
        }
        Player player = Utils.getDamager(event.getDamager());
        Logger logger = this.getManager().getLoggers().get(entity.getUniqueId());
        if (player == null) {
            return;
        }
        if (!this.getInstance().getTeamManager().canHit(player, logger.getPlayer(), false)) {
            event.setCancelled(true);
        }
    }

    private String format(Player player) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        return Config.DEATH_FORMAT.replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(user.getKills()));
    }

    private void handleDeathMessage(Logger logger) {
        Player killer = logger.getVillager().getKiller();
        if (killer != null) {
            Bukkit.broadcastMessage(Config.DEATH_LOGGER_KILLER.replaceAll("%player%", this.format(logger.getPlayer())).replaceAll("%killer%", this.format(killer)));
        } else {
            Bukkit.broadcastMessage(Config.DEATH_LOGGER.replaceAll("%player%", this.format(logger.getPlayer())));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (!this.getManager().getLoggers().containsKey(entity.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        if (event.getBlock().getType() != ItemUtils.getMat("STONE_PLATE")) {
            return;
        }
        if (!(entity instanceof Villager)) {
            return;
        }
        if (!this.getManager().getLoggers().containsKey(entity.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        TimerManager timerManager = this.getInstance().getTimerManager();
        if (player.isDead()) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (player.hasMetadata("loggedout")) {
            return;
        }
        if (team instanceof SafezoneTeam) {
            return;
        }
        if (this.getInstance().getDeathbanManager().isDeathbanned(player)) {
            return;
        }
        if (timerManager.getPvpTimer().hasTimer(player)) {
            return;
        }
        if (timerManager.getInvincibilityTimer().hasTimer(player)) {
            return;
        }
        if (timerManager.getSotwTimer().isActive()) {
            return;
        }
        this.getManager().spawnLogger(player);
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (!this.getManager().getLoggers().containsKey(entity.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }
}
