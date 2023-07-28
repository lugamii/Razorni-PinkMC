package dev.razorni.core.profile.punishment.command;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.PunishmentsClearPacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.uuid.UniqueIDCache;
import dev.razorni.core.util.command.Param;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import dev.razorni.core.profile.Profile;

import java.util.Map;
import java.util.UUID;

public class ClearPunishmentsCommand {

    @dev.razorni.core.util.command.Command(names = "clearallpunishments", permission = "gravity.command.clearpunishments", async = true)
    public static void clearpunishments(CommandSender sender, @Param(name = "target", defaultValue = "all")String profile) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(CC.RED + "This command can only be executed from the console!");
        } else {
            // If it is for a single profile only
            if (!profile.equalsIgnoreCase("all")) {
                // If it is for a single profile only

                Command.broadcastCommandMessage(sender, CC.YELLOW + "Cleared all punishments for " + profile + '.');

//                new PunishmentRemovePacket(new JsonBuilder().addProperty("uuid", profile.getUuid().toString())).send();
//                    Core.getInstance().getPacketBase().sendPacket(new PacketRemovePunishments(profile.getUuid()));
                new PunishmentsClearPacket(false, Profile.getByUuid(UniqueIDCache.uuid(profile))).send();
                return;
            }
            sender.sendMessage("Attempting to clear all punishments. This could take a few seconds...");
            UpdateResult result = Core.getInstance().getMongoHandler().getMongoDatabase()
                    .getCollection("profiles")
                    .updateMany(new Document(), new Document("$set", new Document("punishments", "[]")), new UpdateOptions().upsert(false));
            if (result.wasAcknowledged()) {
                sender.sendMessage(CC.AQUA + "Cleared all punishments. \n * Result was acknowledged \n * Modified " + result.getModifiedCount() + " documents");
                for (Map.Entry<UUID, Profile> entry : Profile.getProfiles().entrySet()) {

                    entry.getValue().getPunishments().clear();
                    entry.getValue().save();
                }
                new PunishmentsClearPacket(true, null).send();
            } else {
                sender.sendMessage(CC.RED + "Couldn't clear all punishments\n" + CC.RED + CC.BOLD + "The result was not acknowledged");
            }
        }
    }

}
