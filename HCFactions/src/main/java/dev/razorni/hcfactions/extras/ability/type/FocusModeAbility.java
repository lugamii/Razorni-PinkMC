package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FocusModeAbility extends Ability {
    private double multiplier;
    private Map<UUID, UUID> focusMode;
    private int hitsValid;
    private Map<UUID, Pair<UUID, Long>> lastDamage = new HashMap<UUID, Pair<UUID, Long>>();
    private int seconds;

    public FocusModeAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Focus Mode");
        this.focusMode = new HashMap<>();
        this.multiplier = this.getAbilitiesConfig().getDouble("FOCUS_MODE.DAMAGE_MULTIPLIER");
        this.seconds = this.getAbilitiesConfig().getInt("FOCUS_MODE.SECONDS");
        this.hitsValid = this.getAbilitiesConfig().getInt("FOCUS_MODE.HITS_VALID");
        Tasks.executeScheduled(this.getManager(), 6000, this::cleanDamageStore);
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        Player damager = this.getDamager(player, this.hitsValid);
        if (damager == null) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.FOCUS_MODE.NO_LAST_HIT"));
            return;
        }
        this.focusMode.put(player.getUniqueId(), damager.getUniqueId());
        this.takeItem(player);
        this.applyCooldown(player);
        Tasks.executeLater(this.getManager(), 20 * this.seconds, () -> {
            this.focusMode.remove(player.getUniqueId());
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.FOCUS_MODE.EXPIRED")) {
                player.sendMessage(s);
            }
        });
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.FOCUS_MODE.USED")) {
            player.sendMessage(s.replaceAll("%player%", damager.getName()));
        }
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.FOCUS_MODE.BEEN_HIT")) {
            damager.sendMessage(s.replaceAll("%player%", player.getName()));
        }
    }

    private void cleanDamageStore() {
        Iterator<Map.Entry<UUID, Pair<UUID, Long>>> iterator = this.lastDamage.entrySet().iterator();
        while (iterator.hasNext()) {
            Pair<UUID, Long> pair = iterator.next().getValue();
            boolean b = (System.currentTimeMillis() - pair.getValue() <= 60000L);
            if (!b)
                iterator.remove();
        }
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
        if (!this.focusMode.containsKey(damager.getUniqueId())) {
            return;
        }
        if (!this.focusMode.get(damager.getUniqueId()).equals(player.getUniqueId())) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        event.setDamage(event.getFinalDamage() * this.multiplier);
    }

    @EventHandler
    public void onDamageStore(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = Utils.getDamager(event.getDamager());
        if (damager == null) {
            return;
        }
        if (damager == player) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        this.lastDamage.put(player.getUniqueId(), new Pair<>(damager.getUniqueId(), System.currentTimeMillis()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.focusMode.remove(player.getUniqueId());
    }

    public Player getDamager(Player player, int time) {
        Pair<UUID, Long> pair = this.lastDamage.get(player.getUniqueId());
        if (pair != null) {
            Player target = Bukkit.getPlayer(pair.getKey());
            boolean b = System.currentTimeMillis() - pair.getValue() <= time * 1000L;
            if (target != null && b) {
                return target;
            }
        }
        return null;
    }
}
