package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PingCommand extends Command {
    public PingCommand(CommandManager manager) {
        super(manager, "ping");
    }

    public String getColor(int ping) {
        if (ping > 100 && ping < 160) {
            return "&e";
        }
        if (ping > 160) {
            return "&c";
        }
        return "&a";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            Player player = (Player) sender;
            int ping = this.getInstance().getVersionManager().getVersion().getPing(player);
            this.sendMessage(sender, this.getLanguageConfig().getString("PING_COMMAND.SELF_PING").replaceAll("%color%", CC.t(this.getColor(ping))).replaceAll("%ping%", String.valueOf(new StringBuilder().append(ping).append("ms"))));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        int tPing = this.getInstance().getVersionManager().getVersion().getPing(target);
        this.sendMessage(sender, this.getLanguageConfig().getString("PING_COMMAND.OTHER_PING").replaceAll("%color%", CC.t(this.getColor(tPing))).replaceAll("%player%", target.getName()).replaceAll("%ping%", String.valueOf(new StringBuilder().append(tPing).append("ms"))));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("ms");
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
