package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetEndCommand extends Command {
    public SetEndCommand(CommandManager manager) {
        super(manager, "setend");
        this.setPermissible("azurite.setend");
        this.completions.add(new TabCompletion(Arrays.asList("spawn", "exit", "worldexit"), 0));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SET_END_COMMAND.USAGE");
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
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Location location = player.getLocation();
        switch (args[0].toLowerCase()) {
            case "exit": {
                if (location.getWorld().getEnvironment() != World.Environment.THE_END) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.WRONG_WORLD").replaceAll("%world%", World.Environment.THE_END.name()));
                    return;
                }
                this.getInstance().getWaypointManager().setEndExit(location);
                this.getConfig().set("LOCATIONS.END_EXITS.END_EXIT", Serializer.serializeLoc(location));
                this.getConfig().save();
                this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.UPDATED"));
                return;
            }
            case "spawn": {
                if (location.getWorld().getEnvironment() != World.Environment.THE_END) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.WRONG_WORLD").replaceAll("%world%", World.Environment.THE_END.name()));
                    return;
                }
                this.getInstance().getWaypointManager().setEndSpawn(location);
                HCF.getPlugin().getMapConfig().setEndSpawn(location);
                this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.UPDATED"));
                return;
            }
            case "worldexit": {
                if (location.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.WRONG_WORLD").replaceAll("%world%", World.Environment.NORMAL.name()));
                    return;
                }
                this.getInstance().getWaypointManager().setEndWorldExit(location);
                this.getConfig().set("LOCATIONS.END_EXITS.WORLD_EXIT", Serializer.serializeLoc(location));
                this.getConfig().save();
                this.sendMessage(sender, this.getLanguageConfig().getString("SET_END_COMMAND.UPDATED"));
                return;
            }
        }
        this.sendUsage(sender);
    }
}
