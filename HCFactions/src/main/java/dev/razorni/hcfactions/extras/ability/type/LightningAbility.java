package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LightningAbility extends Ability {
    private int seconds;
    private int chance;
    private List<UUID> lightnings;
    private double damage;

    public LightningAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Lightning");
        this.lightnings = new ArrayList<>();
        this.chance = this.getAbilitiesConfig().getInt("LIGHTNING.CHANCE");
        this.seconds = this.getAbilitiesConfig().getInt("LIGHTNING.SECONDS");
        this.damage = this.getAbilitiesConfig().getDouble("LIGHTNING.DAMAGE") * 2.0;
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        this.lightnings.add(player.getUniqueId());
        this.takeItem(player);
        this.applyCooldown(player);
        Tasks.executeLater(this.getManager(), 20 * this.seconds, () -> {
            this.lightnings.remove(player.getUniqueId());
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.LIGHTNING.EXPIRED")) {
                player.sendMessage(s);
            }
        });
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.LIGHTNING.USED")) {
            player.sendMessage(s);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();
        if (this.cannotHit(damager, player)) {
            return;
        }
        if (this.lightnings.contains(damager.getUniqueId()) && ThreadLocalRandom.current().nextInt(101) <= this.chance) {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.setHealth(Math.max(player.getHealth() - this.damage, 0.0));
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.LIGHTNING.STRUCK_LIGHTNING")) {
                damager.sendMessage(s.replaceAll("%player%", player.getName()));
            }
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.LIGHTNING.STRUCK_BY_LIGHTNING")) {
                player.sendMessage(s.replaceAll("%player%", damager.getName()));
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.lightnings.remove(player.getUniqueId());
    }
}
