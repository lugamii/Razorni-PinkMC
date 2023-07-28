package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortableArcherAbility extends Ability {
    private Map<UUID, UUID> usedPortableArcher;

    public PortableArcherAbility(AbilityManager manager) {
        super(manager, null, "Portable Archer");
        this.usedPortableArcher = new HashMap<>();
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack stack = event.getItem();
        if (!stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasDisplayName()) {
            return;
        }
        ItemMeta stackMeta = stack.getItemMeta();
        ItemMeta itemMeta = this.item.getItemMeta();
        if (stackMeta.getDisplayName().equals(itemMeta.getDisplayName()) && stackMeta.getLore().equals(itemMeta.getLore())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        Player player = Utils.getDamager(event.getDamager());
        if (player == null) {
            return;
        }
        UUID uuid = this.usedPortableArcher.remove(player.getUniqueId());
        if (uuid != null && event.getDamager().getUniqueId().equals(uuid)) {
            this.getInstance().getClassManager().getArcherClass().archerTag(event);
            this.applyCooldown(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
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
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        ItemStack bow = event.getBow();
        if (this.hasAbilityInHand(player)) {
            this.usedPortableArcher.put(player.getUniqueId(), event.getProjectile().getUniqueId());
            this.getManager().setData(bow, this.getManager().getData(bow) + 1);
            if (this.getManager().getData(bow) == bow.getType().getMaxDurability()) {
                this.getManager().takeItemInHand(player, 1);
            }
        }
    }
}
