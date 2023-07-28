package dev.razorni.hcfactions.timers.command.timer;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.timers.command.timer.args.TimerAddArg;
import dev.razorni.hcfactions.timers.command.timer.args.TimerRemoveArg;

import java.util.Arrays;
import java.util.List;

public class TimerCommand extends Command {
    public TimerCommand(CommandManager manager) {
        super(manager, "timer");
        this.setPermissible("azurite.timer");
        this.handleArguments(Arrays.asList(new TimerRemoveArg(manager), new TimerAddArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TIMER_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("timers", "cooldown");
    }
}