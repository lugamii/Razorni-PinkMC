package dev.razorni.core.util.uuid;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

final class UUIDListener
        implements Listener {
    UUIDListener() {
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UniqueIDCache.update(event.getUniqueId(), event.getName());
    }
}

