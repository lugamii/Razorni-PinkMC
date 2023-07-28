package dev.razorni.hcfactions.teams.commands.team.args.staff;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamTeleportArg extends Argument {
    public TeamTeleportArg(CommandManager manager) {
        super(manager, Arrays.asList("tp", "teleport"));
        this.setPermissible("azurite.team.teleport");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getStringTeams().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Team team = this.getInstance().getTeamManager().getTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (team.getHq() == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_TELEPORT.NO_HQ"));
            return;
        }
        player.teleport(team.getHq().add(0.5, 0.0, 0.5));
        this.sendMessage(sender, this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_TELEPORT.TELEPORTED").replaceAll("%team%", team.getName()));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_TELEPORT.USAGE");
    }
}
