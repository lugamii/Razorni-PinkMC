package dev.razorni.hcfactions.timers.command.customtimer;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.timers.command.customtimer.args.CTimerCreateArg;
import dev.razorni.hcfactions.timers.command.customtimer.args.CTimerDeleteArg;
import dev.razorni.hcfactions.timers.command.customtimer.args.CTimerListArg;

import java.util.Arrays;
import java.util.List;

public class CTimerCommand extends Command {
    public CTimerCommand(CommandManager manager) {
        super(manager, "customtimer");
        this.setPermissible("azurite.customtimer");
        this.handleArguments(Arrays.asList(new CTimerCreateArg(manager), new CTimerDeleteArg(manager), new CTimerListArg(manager)));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("ct", "customtimers", "servertimer");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CTIMER_COMMAND.USAGE");
    }
}