package dev.razorni.core.profile.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    BLACKLIST("blacklisted", "unblacklisted", true, true, new PunishmentTypeData("Blacklists", ChatColor.DARK_RED, Material.INK_SACK, 0)),
    BAN("banned", "unbanned", true, true, new PunishmentTypeData("Bans", ChatColor.GOLD, Material.INK_SACK, 14)),
    MUTE("muted", "unmuted", false, true, new PunishmentTypeData("Mutes", ChatColor.YELLOW, Material.INK_SACK, 11)),
    WARN("warned", null, false, false, new PunishmentTypeData("Warnings", ChatColor.GREEN, Material.EMERALD, 0)),
    KICK("kicked", null, false, false, new PunishmentTypeData("Kicks", ChatColor.GRAY, Material.INK_SACK, 1));

    private String context;
    private String undoContext;
    private boolean ban;
    private boolean canBePardoned;
    private PunishmentTypeData typeData;

    @AllArgsConstructor
    @Getter
    public static class PunishmentTypeData {

        private String readable;
        private ChatColor color;
        private Material material;
        private int data;

    }

}
