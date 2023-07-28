package dev.razorni.crates.lootbox.profile;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class LootBoxProfile {

    private static final ConcurrentHashMap<UUID, LootBoxProfile>
            PROFILE_MAP = new ConcurrentHashMap<>();

    private final UUID uuid;

    private boolean opening;

    public LootBoxProfile(UUID uuid) {
        this.uuid = uuid;

        PROFILE_MAP.put(uuid, this);
    }

    public static LootBoxProfile getLootBoxProfile(UUID uuid) {
        return PROFILE_MAP.get(uuid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void removeProfile() {
        PROFILE_MAP.remove(uuid);
    }
}
