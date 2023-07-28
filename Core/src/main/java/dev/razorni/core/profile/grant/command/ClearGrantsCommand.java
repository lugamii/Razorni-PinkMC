package dev.razorni.core.profile.grant.command;
import dev.razorni.core.database.redis.packets.GrantsClearPacket;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import dev.razorni.core.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ClearGrantsCommand {

    @Command(names = "cleargrants", permission = "gravity.command.cleargrants", async = true)
    public static void cleargrants(CommandSender sender, @Param(name = "player") Profile profile) {

        profile.getGrants().clear();
        profile.save();

        new GrantsClearPacket(profile).send();

        sender.sendMessage(ChatColor.GREEN + "Cleared grants of " + profile.getPlayer().getName() + "!");
    }

}
