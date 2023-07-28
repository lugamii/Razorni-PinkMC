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

public class FeedCommand extends Command {
    public FeedCommand(CommandManager manager) {
        super(manager, "feed");
        this.setPermissible("azurite.feed");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("FEED_COMMAND.USAGE");
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
                Logger.info(sender.getName() + " has fed themselves. (/feed)");
                player.setFoodLevel(20);
                player.sendMessage(this.getLanguageConfig().getString("FEED_COMMAND.FED_SELF"));
                return;
            }
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            Logger.info(sender.getName() + " has fed " + target.getName() + ". (/feed)");
            target.setFoodLevel(20);
            target.sendMessage(this.getLanguageConfig().getString("FEED_COMMAND.TARGET_MESSAGE").replaceAll("%player%", sender.getName()));
            this.sendMessage(sender, this.getLanguageConfig().getString("FEED_COMMAND.FED_TARGET").replaceAll("%player%", target.getName()));
        }
    }
}
