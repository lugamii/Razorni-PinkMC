package dev.razorni.hcfactions.teams.commands.team.args.staff;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamSetPointsArg extends Argument {
    public TeamSetPointsArg(CommandManager manager) {
        super(manager, Collections.singletonList("setpoints"));
        this.setPermissible("azurite.team.setpoints");
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETPOINTS.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayerOrTeam(args[0]);
        Integer points = this.getInt(args[1]);
        if (team == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_NOT_FOUND").replaceAll("%team%", args[0]));
            return;
        }
        if (points == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        team.setPoints(points);
        team.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETPOINTS.SETPOINTS").replaceAll("%team%", args[0]).replaceAll("%amount%", args[1]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getStringTeams().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
