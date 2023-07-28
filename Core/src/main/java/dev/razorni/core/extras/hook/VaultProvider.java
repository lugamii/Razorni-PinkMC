package dev.razorni.core.extras.hook;


import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultProvider extends Permission {

    @Override
    public String getName() {
        return "E";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerAddGroup(Player player, String group) {
        Rank rank = Rank.getRankByDisplayName(group);

        if (rank != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                profile.getGrants().add(new Grant(UUID.randomUUID(), rank, null,
                        System.currentTimeMillis(), "VaultAPI", Integer.MAX_VALUE));
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String playerName, String group) {
        Rank rank = Rank.getRankByDisplayName(group);

        if (rank != null) {
            Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    profile.getGrants().add(new Grant(UUID.randomUUID(), rank, null,
                            System.currentTimeMillis(), "VaultAPI", Integer.MAX_VALUE));
                }
            }
        }

        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        List<String> rankNames = new ArrayList<>();

        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                for (Grant grant : profile.getGrants()) {
                    if (!grant.isRemoved() && !grant.hasExpired()) {
                        if (!rankNames.contains(grant.getRank().getDisplayName())) {
                            rankNames.add(grant.getRank().getDisplayName());
                        }
                    }
                }
            }
        }

        return new String[0];
    }

    @Override
    public boolean playerHas(String world, String playerName, String permission) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                for (Grant grant : profile.getGrants()) {
                    if (!grant.isRemoved() && !grant.hasExpired()) {
                        if (grant.getRank().getAllPermissions().contains(permission)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean playerAdd(String world, String playerName, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(String world, String playerName, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        Rank rank = Rank.getRankByDisplayName(group);

        if (rank != null) {
            return rank.getAllPermissions().contains(permission);
        }

        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        Rank rank = Rank.getRankByDisplayName(group);

        if (rank != null) {
            if (!rank.hasPermission(permission)) {
                rank.addPermission(permission);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        Rank rank = Rank.getRankByDisplayName(group);

        if (rank != null) {
            if (rank.hasPermission(permission)) {
                rank.deletePermission(permission);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean playerInGroup(String world, String playerName, String group) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                for (Grant grant : profile.getGrants()) {
                    if (grant.getRank().getDisplayName().equalsIgnoreCase(group)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String playerName, String group) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                for (Grant grant : profile.getGrants()) {
                    if (grant.getRank().getDisplayName().equalsIgnoreCase(group)) {
                        grant.setRemoved(true);
                        grant.setRemovedBy(null);
                        grant.setRemovedAt(System.currentTimeMillis());
                        grant.setRemovedReason("VaultAPI");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                return profile.getActiveGrant().getRank().getDisplayName();
            }
        }

        return "";
    }

    @Override
    public String[] getGroups() {
        return new ArrayList<>(Rank.getRanks().values()).stream()
                .map(Rank::getDisplayName)
                .collect(Collectors.toList())
                .toArray(new String[Rank.getRanks().values().size()]);
    }
}
