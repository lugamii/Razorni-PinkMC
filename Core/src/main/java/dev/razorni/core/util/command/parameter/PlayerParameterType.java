package dev.razorni.core.util.command.parameter;

import dev.razorni.core.util.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerParameterType
        implements ParameterType<Player> {
    @Override
    public Player transform(CommandSender sender, String value) {
        if (sender instanceof Player && (value.equalsIgnoreCase("self") || value.equals(""))) {
            return (Player) sender;
        }
        Player player=Bukkit.getServer().getPlayer(value);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + value + "\" found.");
            return null;
        }
        return player;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions=new ArrayList<String>();
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if (player.hasMetadata("modmode") || player.hasMetadata("invisible")) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

