package dev.razorni.core.profile.punishment.command;


import dev.razorni.core.util.Locale;
import dev.razorni.core.database.redis.packets.punish.PunishmentAddPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.duration.Duration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Flag;
import dev.razorni.core.util.command.Param;

import java.util.UUID;

public class BlacklistCommand {

    @Command(names = {"blacklist", "ipban","banip"}, permission = "gravity.command.blacklist", async = true)
    public static void ban(CommandSender sender, @Flag(value = {"c", "clear"}, description = "Clear the player's inventory") boolean clear, @Flag(value = {"s", "silent"}, description = "Make the blacklist silent") boolean silent, @Param(name = "target") UUID target, @Param(name = "reason", wildcard = true) String reason) {

        Profile profile = Profile.getByUuid(target);

        if (profile != null && profile.isLoaded()) {
            if (profile.getActiveBan() != null) {
                sender.sendMessage(CC.RED + "That player is already banned.");
            } else {
                String staffName = sender instanceof Player ? (Profile.getProfiles().get(((Player)sender).getUniqueId())).getColoredUsername() : CC.DARK_RED + "Console";
                Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BLACKLIST, System.currentTimeMillis(), reason, Duration.fromString("perm").getValue());
                if (sender instanceof Player) {
                    punishment.setAddedBy(((Player)sender).getUniqueId());
                }

                profile.getPunishments().add(punishment);
                profile.save();

                new PunishmentAddPacket(punishment, profile, staffName, profile.getColoredUsername(), !silent, clear).send();

//                profile.getPunishments().add(punishment);
//                profile.getOptions().setFrozen(false);
//                profile.save();
//
//                JsonBuilder builder = new JsonBuilder();
//                builder.addProperty("uuid", punishment.getUuid().toString());
//                builder.addProperty("target", profile.getColoredUsername());
//                builder.addProperty("targetUUID", profile.getUuid().toString());
//                builder.addProperty("silent", false);
//                builder.addProperty("undo", false);
//                builder.addProperty("type", "BLACKLIST");
//                builder.addProperty("addedBy", staffName);
//                builder.addProperty("addedAt", punishment.getAddedAt());
//                builder.addProperty("addedReason", punishment.getAddedReason());
//                builder.addProperty("duration", punishment.getDuration());
//
//                new PunishPacket(builder).send();
//                Core.getInstance().getPacketBase().sendPacket(new PacketBroadcastPunishment(punishment, staffName, profile.getColoredUsername(), profile.getUuid(), false));


                if (sender instanceof Player) {
                    Profile p = Profile.getByUuid(((Player)sender).getUniqueId());
                    p.getStaffInfo().setBlacklists(p.getStaffInfo().getBlacklists() + 1);
                    p.getStaffInfo().getPunishments().add(punishment);
                }

//                Player player = profile.getPlayer();
//                if (player != null) {
//                    if (clear) {
//                        player.getInventory().clear();
//                        player.updateInventory();
//                    }
//                    player.sendMessage(punishment.getKickMessage());
//                    player.kickPlayer(punishment.getKickMessage());
//                }

            }
        } else {
            sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
        }
    }
}

