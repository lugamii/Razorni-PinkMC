package dev.razorni.hcfactions.utils.commandapi.command.parameter;

import dev.razorni.hcfactions.utils.commandapi.command.ParameterType;
import dev.razorni.hcfactions.HCF;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return HCF.getPlugin().getServer().getOfflinePlayer(source);
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

