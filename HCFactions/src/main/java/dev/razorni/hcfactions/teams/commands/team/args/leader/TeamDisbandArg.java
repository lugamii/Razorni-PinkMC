package dev.razorni.hcfactions.teams.commands.team.args.leader;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamDisbandArg extends Argument {
    public TeamDisbandArg(CommandManager manager) {
        super(manager, Collections.singletonList("disband"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (HCF.getPlugin().getEotwHandler().isEndOfTheWorld()) {
            player.sendMessage(CC.RED + "You cannot disband faction while EOTW is active.");
            return;
        }
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.LEADER.getName()));
            return;
        }
        for (Claim claim : team.getClaims()) {
            this.getInstance().getBalanceManager().giveBalance(player, this.getInstance().getTeamManager().getClaimManager().getPrice(claim, true));
        }
        WaypointManager waypointManager = this.getInstance().getWaypointManager();
        for (Player online : team.getOnlinePlayers()) {
            waypointManager.getHqWaypoint().remove(online, team.getHq(), UnaryOperator.identity());
            waypointManager.getRallyWaypoint().remove(online, team.getRallyPoint(), UnaryOperator.identity());
            if (team.getFocus() != null) {
                Team focusedTeam = team.getFocusedTeam();
                waypointManager.getFocusWaypoint().remove(online, focusedTeam.getHq(), w -> w.replaceAll("%team%", focusedTeam.getName()));
            }
        }
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_TEAM"));
        team.delete();
        for (Claim claim : team.getClaims()) {
            this.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
        }
        this.getInstance().getNametagManager().update();
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_BROADCAST").replaceAll("%team%", team.getName()).replaceAll("%player%", HCF.getPlugin().getRankManager().getRankColor(player) + player.getName()));
    }

    @Override
    public String usage() {
        return null;
    }
}
