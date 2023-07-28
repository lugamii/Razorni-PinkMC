package dev.razorni.hcfactions.teams.commands.team.args.co_leader;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamUnallyArg extends Argument {
    public TeamUnallyArg(CommandManager manager) {
        super(manager, Collections.singletonList("unally"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNALLY.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        PlayerTeam fromTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        PlayerTeam toTeam = this.getInstance().getTeamManager().getByPlayerOrTeam(args[0]);
        if (fromTeam == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (toTeam == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (!fromTeam.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (!fromTeam.getAllies().contains(toTeam.getUniqueID())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNALLY.NOT_ALLIED"));
            return;
        }
        fromTeam.getAllies().remove(toTeam.getUniqueID());
        fromTeam.save();
        toTeam.getAllies().remove(fromTeam.getUniqueID());
        toTeam.save();
        fromTeam.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNALLY.UNALLIED").replaceAll("%team%", toTeam.getName()));
        toTeam.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNALLY.UNALLIED_TARGET").replaceAll("%team%", fromTeam.getName()));
        this.getInstance().getNametagManager().update();
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
