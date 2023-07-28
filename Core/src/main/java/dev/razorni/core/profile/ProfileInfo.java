package dev.razorni.core.profile;

import dev.razorni.core.Core;
import dev.razorni.core.util.BukkitReflection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProfileInfo {
    private UUID uuid;
    private String name;

    public ProfileInfo(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public ProfileInfo(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Player toPlayer() {
        Player player = Core.getInstance().getServer().getPlayer(this.getUuid());
        return player != null && player.isOnline() ? player : null;
    }

    public String getDisplayName() {
        Player player = this.toPlayer();
        return player == null ? this.getName() : player.getDisplayName();
    }

    public int getPing() {
        Player player = Core.getInstance().getServer().getPlayer(this.getUuid());
        return player == null ? 0 : BukkitReflection.getPing(player);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
