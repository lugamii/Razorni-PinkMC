package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.listeners.type.StrengthListener;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class StrengthNerfCommand extends Command {
    public StrengthNerfCommand(CommandManager manager) {
        super(manager, "strengthnerf");
        this.setPermissible("azurite.strengthnerf");
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
        Double value = this.getDouble(args[0]);
        if (value == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
            return;
        }
        StrengthListener.strengthNerf = value;
        this.getConfig().set("STRENGTH_FIX.MULTIPLIER", value);
        this.getConfig().save();
        this.sendMessage(sender, this.getLanguageConfig().getString("STRENGTH_NERF_COMMAND.NERFED").replaceAll("%amount%", String.valueOf(value)));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("STRENGTH_NERF_COMMAND.USAGE");
    }
}
