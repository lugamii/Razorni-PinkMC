package dev.razorni.core.commands.staff;


import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand {

    @Command(names = {"clearchat", "cc", "removeallshitinthechat"}, permission = "gravity.command.clearchat")

    public static void clearchat(CommandSender player) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            builder.append("§a §b §c §d §e §f §0 §r \n");
        }

        String clear = builder.toString();

        for (Player player2 : Core.getInstance().getServer().getOnlinePlayers()) {
            if (!player2.hasPermission("gravity.staff")) {
                player2.sendMessage(clear);
            }

            player2.sendMessage(CC.GREEN + "Chat has been cleared!");
        }
    }
}
