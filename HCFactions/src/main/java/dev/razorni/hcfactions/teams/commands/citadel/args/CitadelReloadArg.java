package dev.razorni.hcfactions.teams.commands.citadel.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.CitadelTeam;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CitadelReloadArg extends Argument {
    public CitadelReloadArg(CommandManager manager) {
        super(manager, Collections.singletonList("reload"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        CitadelTeam team = this.getInstance().getTeamManager().getCitadelTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("CITADEL_COMMAND.CITADEL_NOT_FOUND").replaceAll("%citadel%", args[0]));
            return;
        }
        team.saveBlocks();
        this.sendMessage(sender, this.getLanguageConfig().getString("CITADEL_COMMAND.CITADEL_RELOAD.RELOADED").replaceAll("%citadel%", team.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getTeams().values().stream().filter(s -> s instanceof CitadelTeam).map(Team::getName).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("CITADEL_COMMAND.CITADEL_RELOAD.USAGE");
    }
}
