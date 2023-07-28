package dev.razorni.hcfactions.events.koth.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.events.koth.command.args.*;
import dev.razorni.hcfactions.extras.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KothCommand extends Command {
    public KothCommand(CommandManager manager) {
        super(manager, "koth");
        this.setPermissible("azurite.koth");
        this.handleArguments(Arrays.asList(new KothClaimArg(manager), new KothCreateArg(manager), new KothDeleteArg(manager), new KothStartArg(manager), new KothEndArg(manager), new KothSetColorArg(manager), new KothSetMinArg(manager), new KothSetRemArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KOTH_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
