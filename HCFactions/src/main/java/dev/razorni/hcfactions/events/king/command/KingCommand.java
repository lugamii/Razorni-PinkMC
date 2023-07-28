package dev.razorni.hcfactions.events.king.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.events.king.command.args.KingEndArg;
import dev.razorni.hcfactions.events.king.command.args.KingStartArg;
import dev.razorni.hcfactions.extras.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KingCommand extends Command {
    public KingCommand(CommandManager manager) {
        super(manager, "king");
        this.setPermissible("azurite.king");
        this.handleArguments(Arrays.asList(new KingEndArg(manager), new KingStartArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KING_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("ktk");
    }
}
