package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ClearCommand extends Command {
    public ClearCommand(CommandManager manager) {
        super(manager, "clear");
        this.setPermissible("azurite.clear");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CLEAR_COMMAND.USAGE");
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
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Logger.info(player.getName() + " has cleared their inventory. (/clear)");
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.updateInventory();
                this.sendMessage(sender, this.getLanguageConfig().getString("CLEAR_COMMAND.CLEARED"));
                return;
            }
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            Logger.info(sender.getName() + " has cleared the inventory of " + target.getName() + " . (/clear)");
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.updateInventory();
            this.sendMessage(target, this.getLanguageConfig().getString("CLEAR_COMMAND.CLEARED"));
            this.sendMessage(sender, this.getLanguageConfig().getString("CLEAR_COMMAND.CLEARED_TARGET").replaceAll("%player%", target.getName()));
        }
    }
}
