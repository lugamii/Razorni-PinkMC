package dev.razorni.hcfactions.extras.ability.listener;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.extras.ability.type.PocketBardAbility;
import dev.razorni.hcfactions.extras.framework.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener extends Module<AbilityManager> {
    public AbilityListener(AbilityManager manager) {
        super(manager);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        for (Ability ability : this.getManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(player)) {
                continue;
            }
            event.setCancelled(true);
            break;
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
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        ItemStack stack = this.getManager().getItemInHand(damager);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        for (Ability ability : this.getManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(damager)) {
                continue;
            }
            if (ability.getUseType() != AbilityUseType.HIT_PLAYER) {
                continue;
            }
            ability.onHit(damager, damaged);
            break;
        }
    }

    @EventHandler
    public void onCooldown(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        if (event.getAction().name().contains("RIGHT")) {
            for (Ability ability : this.getManager().getAbilities().values()) {
                if (!ability.hasAbilityInHand(player)) {
                    continue;
                }
                if (ability.getUseType() != AbilityUseType.INTERACT) {
                    continue;
                }
                event.setCancelled(true);
                ability.onClick(player);
                break;
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            for (Ability ability : this.getManager().getAbilities().values()) {
                if (ability instanceof PocketBardAbility) {
                    PocketBardAbility pockedBardAbility = (PocketBardAbility) ability;
                    if (pockedBardAbility.getPocketBardInHand(player) != null && pockedBardAbility.getAbilityCooldown().hasTimer(player)) {
                        player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                        break;
                    }
                }
                if (ability.hasAbilityInHand(player) && ability.getAbilityCooldown().hasTimer(player)) {
                    player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                    break;
                }
            }
        }
    }
}
