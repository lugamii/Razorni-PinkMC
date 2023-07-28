package dev.razorni.hcfactions.timers.command.keyall;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.timers.command.keyall.args.KeyAllCreateArg;
import dev.razorni.hcfactions.timers.command.keyall.args.KeyAllDeleteArg;
import dev.razorni.hcfactions.timers.command.keyall.args.KeyAllListArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyAllCommand extends Command {
    public KeyAllCommand(CommandManager manager) {
        super(manager, "keyall");
        this.setPermissible("azurite.keyall");
        this.handleArguments(Arrays.asList(new KeyAllCreateArg(manager), new KeyAllDeleteArg(manager), new KeyAllListArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KEY_ALL_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}