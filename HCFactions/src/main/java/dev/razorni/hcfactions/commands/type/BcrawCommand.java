package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import org.bukkit.Bukkit;

public class BcrawCommand {

    @Command(names = "bcraw1", permission = "")
    public static void kitapply(@Param(name = "message") String message) {
        Bukkit.getServer().broadcastMessage(CC.translate(message));
    }

}
