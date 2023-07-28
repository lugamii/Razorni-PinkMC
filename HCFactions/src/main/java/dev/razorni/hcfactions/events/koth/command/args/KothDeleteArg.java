package dev.razorni.hcfactions.events.koth.command.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KothDeleteArg extends Argument {
    public KothDeleteArg(CommandManager manager) {
        super(manager, Collections.singletonList("delete"));
        this.setPermissible("azurite.koth.delete");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
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
        Koth koth = this.getInstance().getKothManager().getKoth(args[0]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        koth.end();
        koth.delete();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_DELETE.DELETED").replaceAll("%koth%", koth.getName()));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_DELETE.USAGE");
    }
}
