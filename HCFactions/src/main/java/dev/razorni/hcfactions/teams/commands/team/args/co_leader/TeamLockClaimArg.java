package dev.razorni.hcfactions.teams.commands.team.args.co_leader;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.extra.TeamCooldown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamLockClaimArg extends Argument {
    private final TeamCooldown lockCooldown;

    public TeamLockClaimArg(CommandManager manager) {
        super(manager, Collections.singletonList("lockclaim"));
        this.lockCooldown = new TeamCooldown();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        Claim claim = this.getInstance().getTeamManager().getClaimManager().getClaim(player.getLocation());
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!this.getInstance().getTimerManager().getSotwTimer().isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.ONLY_SOTW"));
            return;
        }
        if (!team.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (claim == null || team.getUniqueID() != claim.getTeam()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.NO_OWN"));
            return;
        }
        if (claim.isLocked()) {
            claim.setLocked(false);
            team.save();
            team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.UNLOCKED").replaceAll("%player%", player.getName()));
            return;
        }
        if (this.lockCooldown.hasCooldown(team)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.COOLDOWN").replaceAll("%seconds%", this.lockCooldown.getRemaining(team)));
            return;
        }
        this.lockCooldown.applyCooldown(team, this.getConfig().getInt("TIMERS_COOLDOWN.LOCK_CLAIM"));
        claim.setLocked(true);
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.LOCKED").replaceAll("%player%", player.getName()));
        for (Player online : claim.getPlayers()) {
            if (team.getPlayers().contains(online.getUniqueId())) {
                continue;
            }
            this.getInstance().getTeamManager().getClaimManager().teleportSafe(online);
            this.sendMessage(online, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LOCKCLAIM.TELEPORTED_SAFE"));
        }
    }

    @Override
    public String usage() {
        return null;
    }
}
