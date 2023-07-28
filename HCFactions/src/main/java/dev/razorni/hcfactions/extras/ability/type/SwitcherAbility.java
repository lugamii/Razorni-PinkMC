package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SwitcherAbility extends Ability {
    private int distance;
    private Set<UUID> switchers;

    public SwitcherAbility(AbilityManager manager) {
        super(manager, null, "Switcher");
        this.switchers = new HashSet<>();
        this.distance = this.getAbilitiesConfig().getInt("SWITCHER.DISTANCE");
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
            return;
        }
        this.applyCooldown(player);
        this.switchers.add(player.getUniqueId());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (this.getItem().getType() == ItemUtils.getMat("SNOW_BALL") && !(event.getDamager() instanceof Snowball)) {
            return;
        }
        if (this.getItem().getType() == ItemUtils.getMat("EGG") && !(event.getDamager() instanceof Egg)) {
            return;
        }
        Player damager = Utils.getDamager(event.getDamager());
        Player player = (Player) event.getEntity();
        if (damager == null) {
            return;
        }
        if (!this.switchers.contains(damager.getUniqueId())) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            damager.getInventory().addItem(this.item);
            damager.updateInventory();
            return;
        }
        if (damager.getLocation().distance(player.getLocation()) > this.distance) {
            damager.getInventory().addItem(this.item);
            damager.updateInventory();
            damager.sendMessage(this.getLanguageConfig().getString("ABILITIES.SWITCHER.TOO_FAR"));
            return;
        }
        Location playerLocation = player.getLocation().clone();
        Location damagerLocation = damager.getLocation().clone();
        player.teleport(damagerLocation);
        damager.teleport(playerLocation);
        this.switchers.remove(damager.getUniqueId());
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.SWITCHER.USED")) {
            damager.sendMessage(s.replaceAll("%player%", player.getName()));
        }
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.SWITCHER.BEEN_HIT")) {
            player.sendMessage(s.replaceAll("%player%", damager.getName()));
        }
    }
}
