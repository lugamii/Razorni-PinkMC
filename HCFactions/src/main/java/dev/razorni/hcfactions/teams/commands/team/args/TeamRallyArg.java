package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.Waypoint;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamRallyArg extends Argument {
    public TeamRallyArg(CommandManager manager) {
        super(manager, Collections.singletonList("rally"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        Waypoint waypoint = this.getInstance().getWaypointManager().getRallyWaypoint();
        for (Player online : team.getOnlinePlayers()) {
            waypoint.remove(online, team.getRallyPoint(), UnaryOperator.identity());
            waypoint.send(online, player.getLocation(), UnaryOperator.identity());
        }
        team.setRallyPoint(player.getLocation());
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RALLY.UPDATED").replaceAll("%location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY()));
    }

    @Override
    public String usage() {
        return null;
    }
}
