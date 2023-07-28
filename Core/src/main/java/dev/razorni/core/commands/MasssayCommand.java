package dev.razorni.core.commands;


import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MasssayCommand {

    @Command(names = "masssay", permission = "gravity.command.masssay")
    public static void masssay(Player player, @Param(name = "message", wildcard = true) String message) {
        message = CC.translate(message); // translate the colors
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.chat(message);
        }
        player.sendMessage(CC.GREEN + "Just made all players type '" + message + CC.GREEN + "'");
    }
}
