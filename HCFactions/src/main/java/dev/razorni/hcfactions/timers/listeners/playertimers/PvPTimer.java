package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.*;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class PvPTimer extends PlayerTimer {
    private final Cooldown portalCooldown;

    public PvPTimer(TimerManager manager) {
        super(manager, true, "PvPTimer", "PLAYER_TIMERS.PVP_TIMER", manager.getConfig().getInt("TIMERS_COOLDOWN.PVP_TIMER"));
        this.portalCooldown = new Cooldown(manager);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (this.hasTimer(player)) {
            if (playerTeam != null && playerTeam.getUniqueID().equals(team.getUniqueID()) && this.getConfig().getBoolean("PVP_TIMER.ENTER_OWN_CLAIM")) {
                return;
            }
            if (team instanceof PlayerTeam || team instanceof EventTeam) {
                event.setCancelled(true);
                this.getManager().getEnderpearlTimer().removeTimer(player);
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                player.sendMessage(this.getLanguageConfig().getString("PVP_TIMER.CANNOT_TELEPORT").replaceAll("%claim%", team.getDisplayName(player)));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (team instanceof SafezoneTeam) {
            this.pauseTimer(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            Team team1 = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
            Team team2 = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getFrom());
            if (team1 instanceof SafezoneTeam) {
                if (!this.getPausedCache().containsKey(player.getUniqueId())) {
                    this.pauseTimer(player);
                }
                if (team1 == team2) {
                    return;
                }
                this.pauseTimer(player);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            } else if (team2 instanceof SafezoneTeam) {
                this.unpauseTimer(player);
            }
            if (this.checkEntry(player, team1)) {
                event.setTo(event.getFrom());
                player.sendMessage(this.getLanguageConfig().getString("INVINCIBILITY.CANNOT_ENTER").replaceAll("%claim%", team1.getDisplayName(player)));
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getTarget();
        if (this.hasTimer(player)) {
            event.setTarget(null);
            event.setCancelled(true);
        }
    }

    public boolean checkEntry(Player player, Team team) {
        if (!this.hasTimer(player)) {
            return false;
        }
        if (team instanceof PlayerTeam) {
            PlayerTeam playerTeam = (PlayerTeam) team;
            boolean ownTeam = this.manager.getConfig().getBoolean("INVINCIBILITY.ENTER_OWN_CLAIM");
            if (ownTeam && playerTeam.getPlayers().contains(player.getUniqueId())) {
                return false;
            }
        }
        return !(team instanceof WildernessTeam) && !(team instanceof RoadTeam) && !(team instanceof SafezoneTeam) && !(team instanceof WarzoneTeam);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
        if (event.getTo().getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }
        if (this.hasTimer(player) && !this.getConfig().getBoolean("PVP_TIMER.END_ENTRY")) {
            event.setCancelled(true);
            if (this.portalCooldown.hasCooldown(player)) {
                return;
            }
            player.sendMessage(this.getLanguageConfig().getString("PVP_TIMER.DENIED_END_ENTRY"));
            this.portalCooldown.applyCooldown(player, 3);
        }
        if (team instanceof SafezoneTeam) {
            this.pauseTimer(player);
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getTimer() != this) {
            return;
        }
        this.getInstance().getNametagManager().update();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.POISON) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.MAGIC) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void applyTimer(Player player) {
            super.applyTimer(player);
            Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
            if (team instanceof SafezoneTeam) {
                this.pauseTimer(player);
            }
    }

    @Override
    public void applyTimer(Player player, long time) {
        super.applyTimer(player, time);
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (team instanceof SafezoneTeam) {
            this.pauseTimer(player);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
            player.setSaturation(20.0f);
            player.setFoodLevel(20);
        }
    }
}