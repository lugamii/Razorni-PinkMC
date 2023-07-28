package dev.razorni.hcfactions.timers.command.keyall.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.timers.listeners.servertimers.KeyAllTimer;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.Formatter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class KeyAllCreateArg extends Argument {
    public KeyAllCreateArg(CommandManager manager) {
        super(manager, Arrays.asList("create", "add"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_CREATE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            this.sendUsage(sender);
            return;
        }
        String name = args[0];
        String displayName = CC.t(args[1]);
        Long time = Formatter.parse(args[2]);
        String command = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        if (time == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        if (this.getInstance().getTimerManager().getCustomTimer(name) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_CREATE.ALREADY_EXISTS").replaceAll("%name%", name));
            return;
        }
        String text = displayName.replace("_", " ");
        new KeyAllTimer(this.getInstance().getTimerManager(), name, text, time, command);
        this.sendMessage(sender, this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_CREATE.CREATED").replaceAll("%name%", displayName).replaceAll("%command%", command));
    }
}