package dev.razorni.core.profile.punishment.command;
import dev.razorni.core.database.redis.packets.punish.PunishmentResolvePacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnblacklistCommand {

    @Command(names = "unblacklist", permission = "gravity.command.unblacklist", async = true)
    public static void unblacklist(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "reason", wildcard = true) String reason) {

        Profile profile = Profile.getByUuid(target);

        if (profile.getActiveBlacklist() == null) {
            sender.sendMessage(CC.RED + "That player is not blacklisted!");
            return;
        }

        String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender).getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

        Punishment punishment = profile.getActiveBlacklist();
        punishment.setResolvedAt(System.currentTimeMillis());
        punishment.setResolvedReason(reason);
        punishment.setResolved(true);
        punishment.setResolvedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (sender instanceof Player) {
            Profile p = Profile.getByUuid(((Player) sender).getUniqueId());
            p.getStaffInfo().setPunishmentResolved(p.getStaffInfo().getPunishmentResolved() + 1);
            p.save();
        }

        new PunishmentResolvePacket(punishment, punishment.getResolvedBy(), profile, reason, staffName, profile.getColoredUsername(), true).send();

//        profile.save();
//
//        JsonBuilder builder = new JsonBuilder();
//        builder.addProperty("uuid", punishment.getUuid().toString());
//        builder.addProperty("target", profile.getColoredUsername());
//        builder.addProperty("targetUUID", profile.getUuid().toString());
//        builder.addProperty("silent", true);
//        builder.addProperty("undo", true);
//        builder.addProperty("type", "BLACKLIST");
//        builder.addProperty("resolvedBy", (punishment.getResolvedBy() == null ? "null" : punishment.getResolvedBy().toString()));
//        builder.addProperty("resolvedAt", punishment.getResolvedAt());
//        builder.addProperty("resolvedReason", punishment.getResolvedReason());
//        builder.addProperty("addedBy", staffName);
//        builder.addProperty("addedAt", punishment.getAddedAt());
//        builder.addProperty("addedReason", punishment.getAddedReason());
//        builder.addProperty("duration", punishment.getDuration());
//
//        new PunishPacket(builder).send();

//        Core.getInstance().getPacketBase().sendPacket(new PacketBroadcastPunishment(punishment, staffName, profile.getColoredUsername(), profile.getUuid(), true));
    }
}
