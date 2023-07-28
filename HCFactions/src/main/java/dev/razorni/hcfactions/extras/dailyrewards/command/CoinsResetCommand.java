package dev.razorni.hcfactions.extras.dailyrewards.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CoinsResetCommand extends Command {

    public CoinsResetCommand(CommandManager manager) {
        super(manager, "coinsreset");
        this.setPermissible("azurite.coinsreset");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendMessage(sender, ChatColor.RED + "Usage: /coinsreset <player>");
            return;
        }
        OfflinePlayer target = CC.getPlayer(args[0]);
        User targetUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (targetUser == null) {
            this.sendMessage(sender, ChatColor.RED + "Player has not found.");
            return;
        }
        if (!targetUser.hasCoinsTime()) {
            this.sendMessage(sender, ChatColor.RED + "Player didnt use his daily coins.");
            return;
        }
        this.sendMessage(sender, ChatColor.GREEN + "Successful!");
        targetUser.setCoinsleft(0L);
        targetUser.save();

    }

}
