package dev.razorni.hcfactions.teams.commands.team.args.co_leader;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamUnclaimArg extends Argument {
    public TeamUnclaimArg(CommandManager manager) {
        super(manager, Collections.singletonList("unclaim"));
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
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (team.getClaims().isEmpty()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNCLAIM.NO_CLAIMS"));
            return;
        }
        if (team.isRaidable()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNCLAIM.RAIDABLE"));
            return;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("ALL")) {
            int i = 0;
            for (Claim claim : team.getClaims()) {
                i += this.getInstance().getTeamManager().getClaimManager().getPrice(claim, true);
                this.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
            }
            if (team.getHq() != null) {
                for (Player online : team.getOnlinePlayers()) {
                    this.getInstance().getWaypointManager().getHqWaypoint().remove(online, team.getHq(), UnaryOperator.identity());
                }
                team.setHq(null);
            }
            team.setBalance(team.getBalance() + i);
            team.getClaims().clear();
            team.save();
            team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNCLAIM.UNCLAIMED_ALL").replaceAll("%player%", player.getName()).replaceAll("%balance%", String.valueOf(i)));
            return;
        }
        Claim claim = this.getInstance().getTeamManager().getClaimManager().getClaim(player.getLocation());
        if (!team.getClaims().contains(claim)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNCLAIM.NOT_OWNED"));
            return;
        }
        int price = this.getInstance().getTeamManager().getClaimManager().getPrice(claim, true);
        this.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
        if (team.getHq() != null && claim.contains(team.getHq())) {
            for (Player online : team.getOnlinePlayers()) {
                this.getInstance().getWaypointManager().getHqWaypoint().remove(online, team.getHq(), UnaryOperator.identity());
            }
            team.setHq(null);
        }
        team.setBalance(team.getBalance() + price);
        team.getClaims().remove(claim);
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNCLAIM.UNCLAIMED_LAND").replaceAll("%player%", player.getName()).replaceAll("%x1%", String.valueOf(claim.getX1())).replaceAll("%z1%", String.valueOf(claim.getZ1())).replaceAll("%x2%", String.valueOf(claim.getX2())).replaceAll("%z2%", String.valueOf(claim.getZ2())).replaceAll("%balance%", String.valueOf(price)));
    }

    @Override
    public String usage() {
        return "/t unclaim";
    }
}
