package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BroadcastCommand extends Command {
    public BroadcastCommand(CommandManager manager) {
        super(manager, "bcraw");
        this.setPermissible("azurite.broadcast");
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
        String text = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("BROADCAST_COMMAND.BROADCAST_FORMAT").replaceAll("%message%", CC.t(text)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("BROADCAST_COMMAND.USAGE");
    }
}
