package dev.razorni.hcfactions.teams.commands.systeam.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.teams.type.GlowstoneMountainTeam;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SysTeamCreateArg extends Argument {
    public SysTeamCreateArg(CommandManager manager) {
        super(manager, Collections.singletonList("create"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CREATE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        String arg = args[0];
        if (args[1].equalsIgnoreCase("GLOWSTONE")) {
            new GlowstoneMountainTeam(this.getInstance().getTeamManager(), arg).save();
            this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CREATE.CREATED_TEAM").replaceAll("%team%", args[0]));
            return;
        }
        TeamType type;
        try {
            type = TeamType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException ignored) {
            type = null;
        }
        if (type == TeamType.MOUNTAIN) {
            type = null;
        }
        if (this.getInstance().getTeamManager().getTeam(arg) != null) {
            this.sendMessage(sender, Config.TEAM_ALREADY_EXISTS.replaceAll("%team%", args[0]));
            return;
        }
        if (type == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CREATE.TYPE_INVALID").replaceAll("%type%", args[1]));
            return;
        }
        if (type == TeamType.PLAYER || type == TeamType.WARZONE || type == TeamType.WILDERNESS) {
            this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CREATE.TYPE_INVALID").replaceAll("%type%", args[1]));
            return;
        }
        this.getInstance().getTeamManager().createTeam(arg, type).save();
        this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CREATE.CREATED_TEAM").replaceAll("%team%", args[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            String other = args[args.length - 1];
            return Stream.of(new String[]{"SAFEZONE", "ROAD", "EVENT", "GLOWSTONE", "ORE_MOUNTAIN", "CITADEL"}).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
