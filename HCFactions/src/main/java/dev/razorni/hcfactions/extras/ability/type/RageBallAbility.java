package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class RageBallAbility extends Ability {
    private List<PotionEffect> enemyEffects;
    private List<PotionEffect> friendlyEffects;
    private Map<UUID, Player> ballsThrown;
    private int radius;
    private String rageBallEffect;
    private boolean wholeTeamCooldown;

    public RageBallAbility(AbilityManager manager) {
        super(manager, null, "Rage Ball");
        this.ballsThrown = new HashMap<>();
        this.friendlyEffects = new ArrayList<>();
        this.enemyEffects = new ArrayList<>();
        this.rageBallEffect = this.getAbilitiesConfig().getString("RAGE_BALL.EFFECT");
        this.wholeTeamCooldown = this.getAbilitiesConfig().getBoolean("RAGE_BALL.COOLDOWN_WHOLE_TEAM");
        this.radius = this.getAbilitiesConfig().getInt("RAGE_BALL.RADIUS");
        this.load();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.hasAbilityInHand(player)) {
            return;
        }
        if (this.cannotUse(player) || this.hasCooldown(player)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        Player player = Utils.getDamager(projectile);
        if (player == null) {
            return;
        }
        if (!this.hasAbilityInHand(player)) {
            return;
        }
        this.ballsThrown.put(projectile.getUniqueId(), player);
        this.applyCooldown(player);
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.RAGE_BALL.USED")) {
            player.sendMessage(s);
        }
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Player player = this.ballsThrown.remove(projectile.getUniqueId());
        if (player != null) {
            PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
            PvPClassManager manager = this.getInstance().getClassManager();
            this.getInstance().getVersionManager().getVersion().playEffect(projectile.getLocation(), this.rageBallEffect, null);
            for (Entity entity : projectile.getNearbyEntities(this.radius, this.radius, this.radius)) {
                if (!(entity instanceof Player)) {
                    continue;
                }
                if (player == entity) {
                    for (PotionEffect effect : this.friendlyEffects) {
                        manager.addEffect(player, effect);
                    }
                } else {
                    Player p = (Player) entity;
                    if (team != null && team.getPlayers().contains(p.getUniqueId())) {
                        if (this.wholeTeamCooldown) {
                            this.abilityCooldown.applyTimer(p);
                        }
                        for (PotionEffect effect : this.friendlyEffects) {
                            manager.addEffect(p, effect);
                        }
                    } else {
                        if (this.cannotHit(player, p)) {
                            continue;
                        }
                        for (PotionEffect effect : this.enemyEffects) {
                            manager.addEffect(p, effect);
                        }
                    }
                }
            }
        }
    }

    private void load() {
        for (String s : this.getAbilitiesConfig().getStringList("RAGE_BALL.FRIENDLY_EFFECTS")) {
            this.friendlyEffects.add(Serializer.getEffect(s));
        }
        for (String s : this.getAbilitiesConfig().getStringList("RAGE_BALL.ENEMY_EFFECTS")) {
            this.enemyEffects.add(Serializer.getEffect(s));
        }
    }
}
