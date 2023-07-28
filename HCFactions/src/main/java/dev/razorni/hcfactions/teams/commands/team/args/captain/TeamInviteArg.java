package dev.razorni.hcfactions.teams.commands.team.args.captain;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamInviteArg extends Argument {
    public TeamInviteArg(CommandManager manager) {
        super(manager, Arrays.asList("invite", "inv"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.CAPTAIN)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CAPTAIN.getName()));
            return;
        }
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (this.getInstance().getTeamManager().getByPlayer(target.getUniqueId()) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.ALREADY_IN_TEAM"));
            return;
        }
        if (team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.FRIENDLY_MEMBER"));
            return;
        }
        if (team.getInvitedPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.ALREADY_INVITED"));
            return;
        }
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.BROADCAST_INVITE").replaceAll("%player%", target.getName()));
        this.sendMessage(target, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INVITE.MESSAGE_INVITE").replaceAll("%team%", team.getName()).replaceAll("%player%", player.getName()));
        team.getInvitedPlayers().add(target.getUniqueId());
        Tasks.executeLater(this.getManager(), 3600, () -> team.getInvitedPlayers().remove(target.getUniqueId()));
    }
}
