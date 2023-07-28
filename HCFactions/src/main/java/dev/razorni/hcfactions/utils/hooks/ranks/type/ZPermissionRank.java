package dev.razorni.hcfactions.utils.hooks.ranks.type;

import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class ZPermissionRank implements Rank {
    private final ZPermissionsService service;

    public ZPermissionRank() {
        this.service = Bukkit.getServicesManager().load(ZPermissionsService.class);
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }

    @Override
    public String getRankPrefix(Player player) {
        return this.service.getPlayerPrefix(player.getUniqueId());
    }

    @Override
    public String getRankName(Player player) {
        return this.service.getPlayerPrimaryGroup(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return this.service.getPlayerSuffix(player.getUniqueId());
    }
}
