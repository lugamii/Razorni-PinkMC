package dev.razorni.hcfactions.kits.commands.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class KitCreateArg extends Argument {
    public KitCreateArg(CommandManager manager) {
        super(manager, Collections.singletonList("create"));
        this.setPermissible("azurite.kit.create");
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
        String kitName = args[0];
        if (this.getInstance().getKitManager().getKit(kitName) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.ALREADY_EXISTS").replaceAll("%kit%", kitName));
            return;
        }
        Kit kit = new Kit(this.getInstance().getKitManager(), kitName);
        kit.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.CREATED").replaceAll("%kit%", kitName));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.USAGE");
    }
}
