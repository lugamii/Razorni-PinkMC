package dev.razorni.hcfactions.teams.commands.citadel;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.teams.commands.citadel.args.CitadelReloadArg;
import dev.razorni.hcfactions.teams.commands.citadel.args.CitadelRespawnArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CitadelCommand extends Command {
    public CitadelCommand(CommandManager manager) {
        super(manager, "citadel");
        this.setPermissible("azurite.citadel");
        this.handleArguments(Arrays.asList(new CitadelReloadArg(manager), new CitadelRespawnArg(manager)));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CITADEL_COMMAND.USAGE");
    }
}
