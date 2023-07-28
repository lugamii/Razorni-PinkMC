package dev.razorni.core.commands;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StreamingCommand {

    @Command(names = "streaming", permission = "gravity.command.streaming")
    public static void streaming(Player player, @Param(name = "url") String url) {
       Bukkit.broadcastMessage(player.getDisplayName() + CC.PINK + " is currently streaming! " + CC.WHITE + CC.ITALIC + "(" +  url + ")");
    }
}
