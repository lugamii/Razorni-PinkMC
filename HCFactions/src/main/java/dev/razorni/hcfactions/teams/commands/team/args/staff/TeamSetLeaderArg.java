package dev.razorni.hcfactions.teams.commands.team.args.staff;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamSetLeaderArg extends Argument {
    public TeamSetLeaderArg(CommandManager manager) {
        super(manager, Collections.singletonList("setleader"));
        this.setPermissible("azurite.team.setleader");
        this.setAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayerOrTeam(args[0]);
        OfflinePlayer target = CC.getPlayer(args[1]);
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
            return;
        }
        if (!team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETLEADER.NOT_IN_TEAM"));
            return;
        }
        if (team.getLeader().equals(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETLEADER.ALREADY_LEADER"));
            return;
        }
        team.getMember(team.getLeader()).setRole(Role.CO_LEADER);
        team.getMember(target.getUniqueId()).setRole(Role.LEADER);
        team.setLeader(target.getUniqueId());
        team.save();
        team.broadcast(this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETLEADER.ALREADY_LEADER").replaceAll("%player%", target.getName()));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_SETLEADER.USAGE");
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
