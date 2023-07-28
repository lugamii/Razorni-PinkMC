package dev.razorni.hcfactions.events.koth.command.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KothStartArg extends Argument {
    public KothStartArg(CommandManager manager) {
        super(manager, Collections.singletonList("start"));
        this.setPermissible("azurite.koth.start");
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_START.USAGE");
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
        if (this.getInstance().getKothManager().getActiveKoths().size() >= this.getConfig().getInt("KOTHS_CONFIG.MAX_KOTHS_ACTIVE")) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_START.MAX_KOTHS_REACHED"));
            return;
        }
        Koth koth = this.getInstance().getKothManager().getKoth(args[0]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        if (koth.isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_START.ALREADY_ACTIVE"));
            return;
        }
        for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.BROADCAST_START")) {
            Bukkit.broadcastMessage(s.replaceAll("%koth%", koth.getName()).replaceAll("%time%", Formatter.formatMMSS(koth.getMinutes())));
        }
        koth.start();
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_START.STARTED").replaceAll("%koth%", koth.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
