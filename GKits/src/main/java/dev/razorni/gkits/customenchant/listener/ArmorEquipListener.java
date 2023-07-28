package dev.razorni.gkits.customenchant.listener;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import dev.razorni.gkits.profile.Profile;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ArmorEquipListener implements Listener {

    private final GKits plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = plugin.getProfileManager().getProfile(event.getPlayer().getUniqueId());
        profile.setJustJoined(true);
    }

    @EventHandler
    public void onEquip(EquipmentSetEvent event) {
        if (!(event.getHumanEntity() instanceof Player))
            return;

        Player player = (Player) event.getHumanEntity();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        ItemStack newItem = event.getNewItem();
        ItemStack previousItem = event.getPreviousItem();

        if (GKits.get().hasLore(previousItem)) {
            previousItem.getItemMeta().getLore().forEach(s -> {

                CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                if (customEnchant != null) {
                    if (profile.isJustJoined()) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            customEnchant.apply(player);
                            profile.setJustJoined(false);
                        }, 5L);
                    } else customEnchant.apply(player);
                }
            });

        }

        if (GKits.get().hasLore(newItem)) {
            newItem.getItemMeta().getLore().forEach(s -> {
                CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                if (customEnchant != null)
                    customEnchant.remove(player);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled())
            return;

        if (event.getItem().getType() != Material.MILK_BUCKET)
            return;

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(GKits.get(), () -> {
            if (player == null || !player.isOnline())
                return;

            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor == null || armor.getType() == Material.AIR)
                    continue;

                if (!GKits.get().hasLore(armor))
                    continue;

                armor.getItemMeta().getLore().forEach(s -> {
                    CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                    if (customEnchant != null) {
                        customEnchant.remove(player);
                        customEnchant.apply(player);
                    }
                });
            }
        }, 1L);
    }

}
