package dev.razorni.hcfactions.teams.commands.systeam.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SysTeamUnclaimArg extends Argument {
    public SysTeamUnclaimArg(CommandManager manager) {
        super(manager, Collections.singletonList("unclaim"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_UNCLAIM.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Team team = this.getInstance().getTeamManager().getTeam(args[0]);
        Claim claim = this.getInstance().getTeamManager().getClaimManager().getClaim(player.getLocation());
        if (team == null || !this.getInstance().getTeamManager().getSystemTeams().containsKey(team.getUniqueID())) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (claim == null || claim.getTeam() != team.getUniqueID()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_UNCLAIM.NOT_IN_CLAIM").replaceAll("%team%", team.getName()));
            return;
        }
        if (team.getHq() != null && claim.contains(team.getHq())) {
            team.setHq(null);
        }
        this.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
        team.getClaims().remove(claim);
        team.save();
        player.sendMessage(this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_UNCLAIM.UNCLAIMED").replaceAll("%team%", team.getName()));
    }
}
