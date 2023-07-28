package dev.razorni.hcfactions.kits.commands;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.kits.commands.args.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitCommand extends Command {
    public KitCommand(CommandManager manager) {
        super(manager, "kit");
        this.setPermissible("azurite.kit");
        this.handleArguments(Arrays.asList(new KitCreateArg(manager), new KitDeleteArg(manager), new KitSetItemsArg(manager), new KitApplyArg(manager), new KitSetCooldownArg(manager), new KitSetNameArg(manager), new KitListArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KIT_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("kits");
    }
}
