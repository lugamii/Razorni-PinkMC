package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GamemodeCommand
        extends Command {
    public GamemodeCommand(CommandManager manager) {
        super(manager, "gamemode");
        this.completions.add(new TabCompletion(Arrays.asList("c", "s", "survival", "creative"), 0));
        this.setPermissible("azurite.gamemode");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("gm");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("GAMEMODE_COMMAND.USAGE");
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
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "creative":
                case "c": {
                    player.setGameMode(GameMode.CREATIVE);
                    this.sendMessage(sender, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", player.getGameMode().name().toLowerCase()));
                    return;
                }
                case "survival":
                case "s": {
                    player.setGameMode(GameMode.SURVIVAL);
                    this.sendMessage(sender, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", player.getGameMode().name().toLowerCase()));
                    return;
                }
            }
            Logger.info(player.getName() + " has switched gamemodes to " + player.getGameMode().name().toLowerCase() + ". (/gamemode)");
            this.sendUsage(sender);
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "creative":
            case "c": {
                target.setGameMode(GameMode.CREATIVE);
                this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
                this.sendMessage(player, this.getLanguageConfig().getString("GAMEMODE_COMMAND.TARGET_GM_UPDATED").replaceAll("%target%", target.getName()).replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
                return;
            }
            case "survival":
            case "s": {
                target.setGameMode(GameMode.SURVIVAL);
                this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
                this.sendMessage(player, this.getLanguageConfig().getString("GAMEMODE_COMMAND.TARGET_GM_UPDATED").replaceAll("%target%", target.getName()).replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
                return;
            }
        }
        Logger.info(player.getName() + " has switched the gamemode of " + target.getName() + " to " + target.getGameMode().name().toLowerCase() + ". (/gamemode)");
        this.sendUsage(sender);
    }
}