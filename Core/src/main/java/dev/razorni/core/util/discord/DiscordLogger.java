package dev.razorni.core.util.discord;

import org.bukkit.Bukkit;
import dev.razorni.core.Core;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.punishment.Punishment;

import java.awt.*;
import java.io.IOException;

public class DiscordLogger {

    public void logGrantAdd(String granted, Grant grant) throws IOException {

//        UUID uuid = Bukkit.getPlayer(refunded).getUniqueId();

        Webhook webhook = new Webhook(Core.getInstance().getConfig().getString("GRANT_ADDED_LOGGING"));
        webhook.addEmbed(new Webhook.EmbedObject()
                .setAuthor("Grant Report (Added)", null, null)
                .setColor(Color.RED)
                .addField("Player Granted", granted, false)
                .addField("Granted by", Bukkit.getPlayer(grant.getAddedBy()).getName(), false)
                .addField("Reason", grant.getAddedReason(), true)
                .addField("Duration", Punishment.TimeUtils.formatIntoDetailedString((int) (grant.getDuration()/1000)), true)

        );
        webhook.execute();
    }

    public void logGrantRemove(String granted, Grant grant) throws IOException {

//        UUID uuid = Bukkit.getPlayer(refunded).getUniqueId();

        Webhook webhook = new Webhook(Core.getInstance().getConfig().getString("GRANT_REMOVED_LOGGING"));
        webhook.addEmbed(new Webhook.EmbedObject()
                .setAuthor("Grant Report (Removed)", null, null)
                .setColor(Color.RED)
                .addField("Player Granted", granted, false)
                .addField("Grant Removed By", Bukkit.getPlayer(grant.getRemovedBy()).getName(), false)
                .addField("Reason", grant.getRemovedReason(), true)
                .addField("Date", grant.getRemovedAtDate(), true)
        );
        webhook.execute();
    }
}