package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GMCCommand extends Command {
    public GMCCommand(CommandManager manager) {
        super(manager, "gmc");
        this.setPermissible("azurite.gmc");
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
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            Logger.info(player.getName() + " has switched gamemodes to creative. (/gmc)");
            player.setGameMode(GameMode.CREATIVE);
            this.sendMessage(player, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", player.getGameMode().name().toLowerCase()));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        Logger.info(player.getName() + " has switched the gamemode of " + target.getName() + " to creative. (/gmc)");
        target.setGameMode(GameMode.CREATIVE);
        this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
        this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.TARGET_GM_UPDATED").replaceAll("%target%", target.getName()).replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
    }
}
