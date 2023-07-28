package dev.razorni.core.util.command.parameter;

import dev.razorni.core.util.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.util.uuid.UniqueIDCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UUIDParameterType
        implements ParameterType<UUID> {
    @Override
    public UUID transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender).getUniqueId();
        }
        UUID uuid= UniqueIDCache.uuid(source);
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + source + " has never joined the server.");
            return null;
        }
        return uuid;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions=new ArrayList<String>();
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if (player.hasMetadata("modmode")) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

