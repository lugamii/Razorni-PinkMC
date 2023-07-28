package dev.razorni.hcfactions.extras.redeem.command;

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

public class ResetRedeemCommand extends Command {

    public ResetRedeemCommand(CommandManager manager) {
        super(manager, "resetredeem");
        this.setPermissible("azurite.resetredeem");
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
            this.sendUsage(sender);
            return;
        }
        OfflinePlayer target = CC.getPlayer(args[0]);
        User targetUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (targetUser == null) {
            this.sendMessage(sender, ChatColor.RED + "Player has not found.");
            return;
        }
        if (!targetUser.isRedeemed()) {
            this.sendMessage(sender, ChatColor.RED + "Player didnt support anyone this map.");
        }
        this.sendMessage(sender, ChatColor.GREEN + "Successful!");
        targetUser.setRedeemed(false);
        targetUser.save();

    }

}
