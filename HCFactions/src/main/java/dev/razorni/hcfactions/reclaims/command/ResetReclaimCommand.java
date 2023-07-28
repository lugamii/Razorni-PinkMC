package dev.razorni.hcfactions.reclaims.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResetReclaimCommand extends Command {
    public ResetReclaimCommand(CommandManager manager) {
        super(manager, "resetreclaim");
        this.setPermissible("azurite.resetreclaim");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("RESET_RECLAIM_COMMAND.USAGE");
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
        if (args[0].equalsIgnoreCase("ONLINE")) {
            int i = 0;
            for (Player online : Bukkit.getOnlinePlayers()) {
                User user = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                user.setReclaimed(false);
                user.save();
                ++i;
            }
            this.sendMessage(sender, this.getLanguageConfig().getString("RESET_RECLAIM_COMMAND.RESET_RECLAIM_ONLINE").replaceAll("%amount%", String.valueOf(i)));
            return;
        }
        if (args[0].equalsIgnoreCase("ALL")) {
            List<User> users = new ArrayList<>(this.getInstance().getUserManager().getUsers().values());
            int i = 0;
            for (User user : users) {
                user.setReclaimed(false);
                user.save();
                ++i;
            }
            this.sendMessage(sender, this.getLanguageConfig().getString("RESET_RECLAIM_COMMAND.RESET_RECLAIM_ALL").replaceAll("%amount%", String.valueOf(i)));
            return;
        }
        OfflinePlayer target = CC.getPlayer(args[0]);
        User targetUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (targetUser == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (!targetUser.isReclaimed()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RESET_RECLAIM_COMMAND.NOT_RECLAIMED").replaceAll("%player%", target.getName()));
            return;
        }
        targetUser.setReclaimed(false);
        targetUser.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("RESET_RECLAIM_COMMAND.RESET_RECLAIM").replaceAll("%player%", target.getName()));
    }
}
