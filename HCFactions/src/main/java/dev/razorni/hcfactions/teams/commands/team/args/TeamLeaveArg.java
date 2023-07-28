package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamLeaveArg extends Argument {
    public TeamLeaveArg(CommandManager manager) {
        super(manager, Collections.singletonList("leave"));
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
        if (team.checkRole(player, Role.LEADER)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEAVE.CANNOT_LEAVE_LEADER"));
            return;
        }
        if (this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation()) == team) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEAVE.CANNOT_LEAVE_IN_CLAIM"));
            return;
        }
        if (this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEAVE.CANNOT_LEAVE_COMBAT"));
            return;
        }
        team.getPlayers().remove(player.getUniqueId());
        team.getMembers().remove(team.getMember(player.getUniqueId()));
        if (team.getDtr() > team.getMaxDtr()) {
            team.setDtr(team.getMaxDtr());
        }
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEAVE.BROADCAST_TEAM").replaceAll("%player%", player.getName()));
        this.getInstance().getTeamManager().getPlayerTeams().remove(player.getUniqueId());
        WaypointManager manager = this.getInstance().getWaypointManager();
        manager.getHqWaypoint().remove(player, team.getHq(), UnaryOperator.identity());
        manager.getRallyWaypoint().remove(player, team.getRallyPoint(), UnaryOperator.identity());
        if (team.getFocus() != null) {
            Team focusedTeam = team.getFocusedTeam();
            manager.getFocusWaypoint().remove(player, focusedTeam.getHq(), w -> w.replaceAll("%team%", focusedTeam.getName()));
        }
        this.getInstance().getNametagManager().update();
        this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEAVE.LEFT_MESSAGE"));
    }

    @Override
    public String usage() {
        return null;
    }
}
