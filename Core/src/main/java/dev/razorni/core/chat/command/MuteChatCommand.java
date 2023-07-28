package dev.razorni.core.chat.command;

import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;

import org.bukkit.command.CommandSender;

public class MuteChatCommand {

    @Command(names = "mutechat", permission = "core.seniormod", async = true)
    public static void mutechat(CommandSender sender) {
        Core.getInstance().getChat().setPublicChatMuted(!Core.getInstance().getChat().isPublicChatMuted());
        Core.getInstance().getServer().broadcastMessage((CC.PINK + "Public chat has been {context} by " + sender.getName())
                .replace("{context}", Core.getInstance().getChat().isPublicChatMuted() ? "muted" : "unmuted"));
    }

}
