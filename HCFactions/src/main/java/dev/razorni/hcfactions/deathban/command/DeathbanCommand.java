package dev.razorni.hcfactions.deathban.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.deathban.Deathban;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeathbanCommand extends Command {
    public DeathbanCommand(CommandManager manager) {
        super(manager, "deathban");
        this.setPermissible("azurite.deathban");
        this.completions.add(new TabCompletion(Arrays.asList("setarenaspawn", "info", "remove"), 0));
    }

    @Override
    public List<String> usage() {
        return ((CommandManager) this.manager).getLanguageConfig().getStringList("DEATHBAN_COMMAND.USAGE");
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
        switch (args[0].toLowerCase()) {
            case "setarenaspawn": {
                this.getInstance().getDeathbanManager().setArenaSpawn(player.getLocation());
                this.getInstance().getDeathbanManager().save();
                this.sendMessage(sender, this.getLanguageConfig().getString("DEATHBAN_COMMAND.SET_ARENA_SPAWN"));
                return;
            }
            case "info": {
                if (args.length < 2) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("DEATHBAN_COMMAND.DEATHBAN_INFO.USAGE"));
                    return;
                }
                Player target = Bukkit.getPlayer((String) args[1]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                Deathban deathban = this.getInstance().getDeathbanManager().getDeathban(target);
                if (deathban == null) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("DEATHBAN_COMMAND.NOT_DEATHBANNED").replaceAll("%player%", target.getName()));
                    return;
                }
                List<String> list = this.getLanguageConfig().getStringList("DEATHBAN_COMMAND.DEATHBAN_INFO.FORMAT");
                list.replaceAll(s -> s.replaceAll("%player%", target.getName()).replaceAll("%date%", deathban.getDateFormatted()).replaceAll("%reason%", deathban.getReason()).replaceAll("%location%", Utils.formatLocation(deathban.getLocation())));
                for (String s : list) {
                    this.sendMessage(sender, s);
                }
                return;
            }
            case "remove": {
                if (args.length < 2) {
                    this.sendMessage(sender, this.getLanguageConfig().getString(""));
                    return;
                }
                Player target = Bukkit.getPlayer((String) args[1]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                if (!this.getInstance().getDeathbanManager().isDeathbanned(target)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("DEATHBAN_COMMAND.NOT_DEATHBANNED").replaceAll("%player%", target.getName()));
                    return;
                }
                this.getInstance().getDeathbanManager().removeDeathban(target);
                this.sendMessage(sender, this.getLanguageConfig().getString("DEATHBAN_COMMAND.REMOVED_DEATHBAN").replaceAll("%player%", target.getName()));
                return;
            }
        }
        this.sendUsage(sender);
    }
}
