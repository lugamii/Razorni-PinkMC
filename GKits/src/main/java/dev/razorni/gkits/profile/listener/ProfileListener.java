package dev.razorni.gkits.profile.listener;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.profile.Profile;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final GKits plugin;

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        plugin.getProfileManager().loadOrCreate(event.getUniqueId(), profile -> {
        }, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Profile profile = plugin.getProfileManager()
                .getProfile(event.getPlayer().getUniqueId());
        profile.save(true);
    }

}
