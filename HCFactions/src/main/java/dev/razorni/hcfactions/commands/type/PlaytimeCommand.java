package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.listeners.type.MainListener;
import dev.razorni.hcfactions.utils.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(names = "playtime", permission = "")
    public static void playTime(CommandSender sender, @Param(name = "player") Player target) {
        sender.sendMessage(CC.translate("&6&l┃ &f" + target.getName() + "'s Playtime is &6" + Formatter.formatDetailed(HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId()).getPlaytime() + MainListener.getPlaySession().getCurrentSession())));
    }

}
