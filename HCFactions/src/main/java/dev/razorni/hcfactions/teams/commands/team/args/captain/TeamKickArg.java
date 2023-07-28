package dev.razorni.hcfactions.teams.commands.team.args.captain;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamKickArg extends Argument {
    public TeamKickArg(CommandManager manager) {
        super(manager, Collections.singletonList("kick"));
        this.setAsync(true);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.USAGE");
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
        OfflinePlayer target = CC.getPlayer(args[0]);
        Player player = (Player) sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.CAPTAIN)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CAPTAIN.getName()));
            return;
        }
        if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (!team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.NOT_IN_TEAM"));
            return;
        }
        if (player == target) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.CANNOT_KICK_SELF"));
            return;
        }
        if (!team.checkRole(player, team.getMember(target.getUniqueId()).getRole())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.HIGHER_ROLE").replaceAll("%player%", target.getName()));
            return;
        }
        team.getPlayers().remove(target.getUniqueId());
        team.getMembers().remove(team.getMember(target.getUniqueId()));
        if (team.getDtr() > team.getMaxDtr()) {
            team.setDtr(team.getMaxDtr());
        }
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.BROADCAST_TEAM").replaceAll("%player%", target.getName()));
        this.getInstance().getTeamManager().getPlayerTeams().remove(target.getUniqueId());
        if (target.isOnline()) {
            WaypointManager manager = this.getInstance().getWaypointManager();
            Player targetPlayer = target.getPlayer();
            this.getInstance().getNametagManager().update();
            manager.getRallyWaypoint().remove(targetPlayer, team.getRallyPoint(), UnaryOperator.identity());
            manager.getHqWaypoint().remove(targetPlayer, team.getHq(), UnaryOperator.identity());
            if (team.getFocus() != null) {
                Team focusTeam = team.getFocusedTeam();
                manager.getFocusWaypoint().remove(player, focusTeam.getHq(), w -> w.replaceAll("%team%", focusTeam.getName()));
            }
            targetPlayer.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.KICKED_MESSAGE").replaceAll("%team%", team.getName()));
        }
    }
}
