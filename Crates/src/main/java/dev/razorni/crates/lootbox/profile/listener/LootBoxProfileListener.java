package dev.razorni.crates.lootbox.profile.listener;

import dev.razorni.crates.lootbox.profile.LootBoxProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LootBoxProfileListener implements Listener {

    @EventHandler
    public void onRegister(AsyncPlayerPreLoginEvent event) {
        new LootBoxProfile(event.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        LootBoxProfile.getLootBoxProfile(event.getPlayer().getUniqueId())
                .removeProfile();
    }
}
