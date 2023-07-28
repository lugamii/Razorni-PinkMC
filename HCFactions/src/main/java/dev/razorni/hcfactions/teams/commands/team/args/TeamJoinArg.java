package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class TeamJoinArg extends Argument {
    public TeamJoinArg(CommandManager manager) {
        super(manager, Arrays.asList("accept", "join"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getStringTeams().keySet().stream().filter(llllllllllllllllIllIlIlIlIIlllII -> llllllllllllllllIllIlIlIlIIlllII.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.USAGE");
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
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayerOrTeam(args[0]);
        if (this.getInstance().getTeamManager().getByPlayer(player.getUniqueId()) != null) {
            this.sendMessage(sender, Config.ALREADY_IN_TEAM);
            return;
        }
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (!team.getInvitedPlayers().contains(player.getUniqueId()) && !team.isOpen()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.NOT_INVITED").replaceAll("%team%", args[0]));
            return;
        }
        if (team.getPlayers().size() == this.getTeamConfig().getInt("TEAMS.TEAM_SIZE")) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.TEAM_FULL"));
            return;
        }
        if (team.hasRegen() && !this.getInstance().getTimerManager().getEotwTimer().isActive() && !this.getInstance().isKits()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.CANNOT_JOIN_FREEZE"));
            return;
        }
        if (this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.CANNOT_JOIN_COMBAT"));
            return;
        }
        team.getPlayers().add(player.getUniqueId());
        team.getMembers().add(new Member(player.getUniqueId(), Role.MEMBER));
        team.getInvitedPlayers().remove(player.getUniqueId());
        team.setDtr(team.getMaxDtr());
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_JOIN.BROADCAST_JOIN").replaceAll("%player%", player.getName()));
        this.getInstance().getTeamManager().getPlayerTeams().put(player.getUniqueId(), team);
        WaypointManager manager = this.getInstance().getWaypointManager();
        manager.getHqWaypoint().send(player, team.getHq(), UnaryOperator.identity());
        manager.getRallyWaypoint().send(player, team.getRallyPoint(), UnaryOperator.identity());
        if (team.getFocus() != null) {
            Team focusedTeam = team.getFocusedTeam();
            manager.getFocusWaypoint().send(player, focusedTeam.getHq(), w -> w.replaceAll("%team%", focusedTeam.getName()));
        }
        this.getInstance().getNametagManager().update();
    }
}
