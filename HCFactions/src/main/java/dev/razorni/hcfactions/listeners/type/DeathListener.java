package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.pvpclass.type.rogue.RogueBackstabEvent;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener extends Module<ListenerManager> {
    public DeathListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent.DamageCause cause = (player.getLastDamageCause() == null) ? EntityDamageEvent.DamageCause.SUICIDE : player.getLastDamageCause().getCause();
        if (this.getInstance().getDeathbanManager().isDeathbanned(player)) {
            event.setDeathMessage(null);
            event.setDroppedExp(0);
            event.getDrops().clear();
            return;
        }
        player.getWorld().strikeLightningEffect(player.getLocation());
        if (player.getLastDamageCause() instanceof RogueBackstabEvent) {
            RogueBackstabEvent backstabEvent = (RogueBackstabEvent) player.getLastDamageCause();
            this.getInstance().getTeamManager().handleDeath(player, backstabEvent.getBackstabbedBy());
            event.setDeathMessage(this.formatRogueEvent(player, backstabEvent));
        } else if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            this.getInstance().getTeamManager().handleDeath(player, player.getKiller());
            event.setDeathMessage(this.formatDamageEvent(player, (EntityDamageByEntityEvent) player.getLastDamageCause(), cause));
        } else {
            this.getInstance().getTeamManager().handleDeath(player, player.getKiller());
            event.setDeathMessage(this.formatEvent(player, cause));
        }
    }

    private String formatEvent(Player player, EntityDamageEvent.DamageCause cause) {
        Player killer = player.getKiller();
        switch (cause) {
            case BLOCK_EXPLOSION: {
                return Config.DEATH_EXPLOSION.replaceAll("%player%", this.format(player));
            }
            case MAGIC: {
                return Config.DEATH_MAGIC;
            }
            case WITHER: {
                return Config.DEATH_WITHER.replaceAll("%player%", this.format(player));
            }
            case STARVATION: {
                return Config.DEATH_STARVATION.replaceAll("%player%", this.format(player));
            }
            case DROWNING: {
                return Config.DEATH_DROWN.replaceAll("%player%", this.format(player));
            }
            case SUFFOCATION: {
                return Config.DEATH_SUFFOCATION.replaceAll("%player%", this.format(player));
            }
            case POISON: {
                return Config.DEATH_POISON.replaceAll("%player%", this.format(player));
            }
            case LAVA: {
                return Config.DEATH_LAVA.replaceAll("%player%", this.format(player));
            }
            case FIRE_TICK:
            case FIRE: {
                return Config.DEATH_FIRE.replaceAll("%player%", this.format(player));
            }
            case THORNS:
            case CONTACT: {
                return Config.DEATH_CONTACT.replaceAll("%player%", this.format(player));
            }
            case FALL: {
                if (killer != null && killer != player) {
                    return Config.DEATH_FALL_KILLER.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(killer));
                }
                return Config.DEATH_FALL.replaceAll("%player%", this.format(player));
            }
            case VOID: {
                if (killer != null && killer != player) {
                    return Config.DEATH_VOID_KILLER.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(killer));
                }
                return Config.DEATH_VOID.replaceAll("%player%", this.format(player));
            }
            default: {
                return Config.DEATH_DEFAULT.replaceAll("%player%", this.format(player));
            }
        }
    }

    private String formatRogueEvent(Player player, RogueBackstabEvent event) {
        Player backastabber = event.getBackstabbedBy();
        return Config.DEATH_BACKSTABBED.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(backastabber));
    }

    private String format(Player player) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        return Config.DEATH_FORMAT.replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(user.getKills()));
    }

    private String formatDamageEvent(Player player, EntityDamageByEntityEvent event, EntityDamageEvent.DamageCause cause) {
        Player killer = player.getKiller();
        switch (cause) {
            case FALLING_BLOCK: {
                return Config.DEATH_SUFFOCATION.replaceAll("%player%", this.format(player));
            }
            case ENTITY_EXPLOSION: {
                return Config.DEATH_EXPLOSION.replaceAll("%player%", this.format(player));
            }
            case LIGHTNING: {
                return Config.DEATH_LIGHTNING.replaceAll("%player%", this.format(player));
            }
            case ENTITY_ATTACK: {
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    return Config.DEATH_KILLER.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(damager)).replaceAll("%item%", ItemUtils.getItemName(this.getManager().getItemInHand(damager)));
                }
                return Config.DEATH_ENTITY.replaceAll("%player%", this.format(player)).replaceAll("%entity%", event.getEntity().getType().name().toLowerCase());
            }
            case FALL: {
                if (killer != null && killer != player) {
                    return Config.DEATH_FALL_KILLER.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(killer));
                }
                return Config.DEATH_FALL.replaceAll("%player%", this.format(player));
            }
            case PROJECTILE: {
                if (killer != null && killer != player) {
                    int distance = (int) killer.getLocation().distance(player.getLocation());
                    return Config.DEATH_PROJECTILE_KILLER.replaceAll("%player%", this.format(player)).replaceAll("%killer%", this.format(killer)).replaceAll("%blocks%", String.valueOf(distance));
                }
                return Config.DEATH_PROJECTILE.replaceAll("%player%", this.format(player));
            }
            default: {
                return Config.DEATH_DEFAULT.replaceAll("%player%", this.format(player));
            }
        }
    }
}
