package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.Logger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TpLocCommand extends Command {
    public TpLocCommand(CommandManager manager) {
        super(manager, "tploc");
        this.setPermissible("azurite.tploc");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("tppos");
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
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Integer x = this.getInt(args[0]);
        Integer y = this.getInt(args[1]);
        Integer z = this.getInt(args[2]);
        if (x == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
            return;
        }
        if (y == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        if (z == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        Logger.info(player.getName() + " has teleported to " + x + ", " + y + ", " + z + ". (/tploc)");
        player.teleport(new Location(player.getWorld(), (double) x, (double) y, (double) z, player.getLocation().getYaw(), player.getLocation().getPitch()));
        this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPLOC_COMMAND.TELEPORTED").replaceAll("%x%", String.valueOf(x)).replaceAll("%y%", String.valueOf(y)).replaceAll("%z%", String.valueOf(z)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TELEPORT_COMMAND.TPLOC_COMMAND.USAGE");
    }
}
