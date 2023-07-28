package dev.razorni.hcfactions.utils.commandapi.uuid.impl;

import dev.razorni.hcfactions.utils.commandapi.uuid.UUIDCache;
import dev.razorni.hcfactions.HCF;

import java.util.UUID;

public final class BukkitUUIDCache
        implements UUIDCache {
    @Override
    public UUID uuid(String name) {
        return HCF.getPlugin().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return HCF.getPlugin().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(UUID uuid) {
    }

    @Override
    public void update(UUID uuid, String name) {
    }
}

