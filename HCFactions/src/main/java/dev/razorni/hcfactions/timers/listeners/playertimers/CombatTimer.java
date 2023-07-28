package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.Formatter;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class CombatTimer extends PlayerTimer {
    private final Cooldown portalCooldown;

    public CombatTimer(TimerManager manager) {
        super(manager, false, "Combat", "PLAYER_TIMERS.COMBAT_TAG", manager.getConfig().getInt("TIMERS_COOLDOWN.COMBAT_TAG"));
        this.portalCooldown = new Cooldown(manager);
    }

    @Override
    public String getRemainingString(Player player) {
        if (this.pausedCache.containsKey(player.getUniqueId())) {
            return Formatter.formatMMSS(this.pausedCache.get(player.getUniqueId()));
        }
        long time = this.timerCache.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.formatMMSS(time);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.hasTimer(player)) {
            Tasks.executeLater(this.getManager(), 10, () -> this.removeTimer(player));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (this.hasTimer(player) && team instanceof SafezoneTeam) {
            event.setTo(event.getFrom());
            player.sendMessage(this.getLanguageConfig().getString("COMBAT_TIMER.CANNOT_ENTER"));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = Utils.getDamager(event.getDamager());
        Player damaged = (Player) event.getEntity();
        if (damager == null) {
            return;
        }
        if (damager == damaged) {
            return;
        }
        if (this.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            if (!this.hasTimer(damager)) {
                damager.sendMessage(this.getLanguageConfig().getString("COMBAT_TIMER.TAGGED").replaceAll("%seconds%", String.valueOf(this.seconds)));
            }
            if (!this.hasTimer(damaged)) {
                damaged.sendMessage(this.getLanguageConfig().getString("COMBAT_TIMER.TAGGED").replaceAll("%seconds%", String.valueOf(this.seconds)));
            }
            this.applyTimer(damager);
            this.applyTimer(damaged);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
        if (this.hasTimer(player) && team instanceof SafezoneTeam) {
            event.setCancelled(true);
            this.getManager().getEnderpearlTimer().removeTimer(player);
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            player.sendMessage(this.getLanguageConfig().getString("COMBAT_TIMER.CANNOT_TELEPORT"));
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }
        if (this.hasTimer(player) && !this.getConfig().getBoolean("COMBAT_TIMER.END_ENTRY")) {
            event.setCancelled(true);
            if (this.portalCooldown.hasCooldown(player)) {
                return;
            }
            player.sendMessage(this.getLanguageConfig().getString("COMBAT_TIMER.DENIED_END_ENTRY"));
            this.portalCooldown.applyCooldown(player, 3);
        }
    }
}
