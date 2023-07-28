package dev.razorni.gkits.customenchant.listener;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import dev.razorni.gkits.customenchant.impl.HellForgedEnchant;
import dev.razorni.gkits.customenchant.impl.RecoverEnchant;
import cc.invictusgames.ilib.builder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class CustomEnchantListener implements Listener {

    private final GKits plugin;

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (player.hasMetadata("implants")) {
            player.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == player
                || killer == null)
            return;

        ItemStack chestPlate = killer.getInventory().getArmorContents()[2];
        if (chestPlate == null)
            return;

        if (!plugin.hasLore(chestPlate))
            return;

        chestPlate.getItemMeta().getLore().forEach(s -> {
            CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

            if (!(customEnchant instanceof RecoverEnchant))
                return;

            killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 160, 2));
        });
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)
                || (!(event.getEntity() instanceof Player)))
            return;

        Player damager = (Player) event.getDamager();
        ItemStack itemInHand = damager.getItemInHand();

        if (!plugin.hasLore(itemInHand))
            return;

        itemInHand.getItemMeta().getLore().forEach(s -> {
            CustomEnchant customEnchant = plugin.getCustomEnchantManager()
                    .getEnchantByDisplayName(s);

            if (!(customEnchant instanceof HellForgedEnchant))
                return;

            if (itemInHand.getDurability() != 0)
                itemInHand.setDurability((short) 0);
        });
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        if (!player.hasMetadata("hellforged"))
            return;

        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack itemStack : armorContents) {
            if (!GKits.get().hasLore(itemStack))
                return;

            if (itemStack == null || itemStack.getType() == Material.AIR)
                return;

            itemStack.getItemMeta().getLore().forEach(s -> {
                CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                if (!(customEnchant instanceof HellForgedEnchant))
                    return;

                if (itemStack.getDurability() != 0)
                    itemStack.setDurability((short) 0);
            });
        }
    }

    @EventHandler
    public void onDrag(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (cursor.getType() != Material.BOOK
                || !cursor.getItemMeta().hasLore())
            return;

        if (current.getType() == null
                || current.getType() == Material.AIR)
            return;

        if (cursor.getAmount() > 1) {
            player.sendMessage(ChatColor.RED + "You can not apply a book when it's stacked.");
            return;
        }

        CustomEnchant enchant = plugin.getCustomEnchantManager()
                .getEnchantByDisplayName(cursor.getItemMeta().getDisplayName());

        if (!enchant.canApply(player, current, player::sendMessage))
            return;

        ItemBuilder itemBuilder = new ItemBuilder(enchant.addToLore(current));
        event.setCurrentItem(itemBuilder.build());
        player.setItemOnCursor(new ItemStack(Material.AIR));
        event.setCancelled(true);
        player.updateInventory();
    }

}
