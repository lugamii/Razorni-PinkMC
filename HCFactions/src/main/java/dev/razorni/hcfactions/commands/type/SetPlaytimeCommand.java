package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPlaytimeCommand {

    @Command(names = "setplaytime", permission = "azurite.setplaytime")
    public static void playTime(CommandSender sender, @Param(name = "player") Player target, @Param(name = "time") int time) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId());
        profile.setPlaytime(time);
        profile.save();
        sender.sendMessage(CC.GREEN + "You have successfully set " + target.getName() + " playtime.");
    }

}
