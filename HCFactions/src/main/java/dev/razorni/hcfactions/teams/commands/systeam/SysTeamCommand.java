package dev.razorni.hcfactions.teams.commands.systeam;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.teams.commands.systeam.args.*;

import java.util.Arrays;
import java.util.List;

public class SysTeamCommand extends Command {
    public SysTeamCommand(CommandManager manager) {
        super(manager, "systemteam");
        this.setPermissible("azurite.systeam");
        this.handleArguments(Arrays.asList(new SysTeamDeleteArg(manager), new SysTeamCreateArg(manager), new SysTeamClaimArg(manager), new SysTeamSetHqArg(manager), new SysTeamUnclaimArg(manager)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SYSTEM_TEAM_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("systeam", "sysfac", "sysfaction", "st", "sf", "fc");
    }
}
