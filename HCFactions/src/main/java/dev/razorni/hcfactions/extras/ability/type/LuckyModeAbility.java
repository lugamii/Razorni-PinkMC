package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyModeAbility extends Ability {
    private double maximum;
    private int seconds;
    private DecimalFormat formatter;
    private double minimum;
    private Map<UUID, Double> luckyMode;

    public LuckyModeAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Lucky Mode");
        this.luckyMode = new HashMap<>();
        this.formatter = new DecimalFormat("##");
        this.minimum = this.getAbilitiesConfig().getDouble("LUCKY_MODE.MINIMUM_MULTIPLIER");
        this.maximum = this.getAbilitiesConfig().getDouble("LUCKY_MODE.MAXIMUM_MULTIPLIER");
        this.seconds = this.getAbilitiesConfig().getInt("LUCKY_MODE.SECONDS");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!this.luckyMode.containsKey(damager.getUniqueId())) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        double lucky = this.luckyMode.get(damager.getUniqueId());
        if (lucky == 0.0) {
            return;
        }
        if (lucky > 0.0) {
            event.setDamage(event.getFinalDamage() * (lucky / 100.0 + 1.0));
        } else {
            event.setDamage(event.getFinalDamage() * (1.0 / (Math.abs(lucky) / 100.0 + 1.0)));
        }
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        double lucky = ThreadLocalRandom.current().nextDouble(this.minimum, this.maximum + 1.0);
        this.luckyMode.put(player.getUniqueId(), lucky);
        this.takeItem(player);
        this.applyCooldown(player);
        Tasks.executeLater(this.getManager(), 20 * this.seconds, () -> {
            this.luckyMode.remove(player.getUniqueId());
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.LUCKY_MODE.EXPIRED")) {
                player.sendMessage(s);
            }
        });
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.LUCKY_MODE.USED")) {
            player.sendMessage(s.replaceAll("%amount%", this.formatter.format(lucky)));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.luckyMode.remove(player.getUniqueId());
    }
}
