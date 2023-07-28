package dev.razorni.core.util.command.parameter;

import dev.razorni.core.util.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OfflinePlayerParameterType
        implements ParameterType<OfflinePlayer> {
    @Override
    public OfflinePlayer transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return (Player) sender;
        }
        return Core.getInstance().getServer().getOfflinePlayer(source);
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions=new ArrayList<String>();
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if (!sender.canSee(player)) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

