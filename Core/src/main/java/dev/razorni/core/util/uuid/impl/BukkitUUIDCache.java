package dev.razorni.core.util.uuid.impl;

import dev.razorni.core.Core;
import dev.razorni.core.util.uuid.UUIDCache;

import java.util.UUID;

public final class BukkitUUIDCache
        implements UUIDCache {
    @Override
    public UUID uuid(String name) {
        return Core.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return Core.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(UUID uuid) {
    }

    @Override
    public void update(UUID uuid, String name) {
    }
}

