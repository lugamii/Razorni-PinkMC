package dev.razorni.hcfactions.timers.command.customtimer.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.timers.type.CustomTimer;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.Formatter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CTimerCreateArg extends Argument {
    public CTimerCreateArg(CommandManager manager) {
        super(manager, Arrays.asList("create", "add"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            this.sendUsage(sender);
            return;
        }
        String name = args[0];
        String text = CC.t(args[1]);
        Long time = Formatter.parse(args[2]);
        if (time == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        if (this.getInstance().getTimerManager().getCustomTimer(name) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_CREATE.ALREADY_EXISTS").replaceAll("%name%", name));
            return;
        }
        String displayName = text.replace("_", " ");
        new CustomTimer(this.getInstance().getTimerManager(), name, displayName, time);
        this.sendMessage(sender, this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_CREATE.CREATED").replaceAll("%name%", text));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_CREATE.USAGE");
    }
}