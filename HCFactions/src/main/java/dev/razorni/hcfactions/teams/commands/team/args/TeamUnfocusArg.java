package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.Waypoint;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamUnfocusArg extends Argument {
    public TeamUnfocusArg(CommandManager manager) {
        super(manager, Collections.singletonList("unfocus"));
    }

    @Override
    public String usage() {
        return null;
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
        if (team.getFocus() == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.NO_FOCUS"));
            return;
        }
        Waypoint waypoint = this.getInstance().getWaypointManager().getFocusWaypoint();
        PlayerTeam targetTeam = team.getFocusedTeam();
        for (Player online : team.getOnlinePlayers()) {
            waypoint.remove(online, targetTeam.getHq(), w -> w.replaceAll("%team%", targetTeam.getName()));
        }
        team.setFocus(null);
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.FOCUS_CLEARED"));
        this.getInstance().getNametagManager().update();
    }
}
