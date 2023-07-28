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

public class TpCommand extends Command {
    public TpCommand(CommandManager manager) {
        super(manager, "tp");
        this.setPermissible("azurite.teleport");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("teleport");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        Logger.info(player.getName() + " has teleported to " + target.getName() + ". (/teleport)");
        player.teleport(target);
        player.sendMessage(this.getLanguageConfig().getString("TELEPORT_COMMAND.TP_COMMAND.TELEPORTED").replaceAll("%player%", target.getName()));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TELEPORT_COMMAND.TP_COMMAND.USAGE");
    }
}
