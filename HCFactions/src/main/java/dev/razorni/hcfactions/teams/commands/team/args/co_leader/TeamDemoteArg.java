package dev.razorni.hcfactions.teams.commands.team.args.co_leader;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeamDemoteArg extends Argument {
    private final List<Role> roles;

    public TeamDemoteArg(CommandManager manager) {
        super(manager, Collections.singletonList("demote"));
        this.roles = new ArrayList<>(Arrays.asList(Role.values()));
        Collections.reverse(this.roles);
        this.setAsync(true);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.USAGE");
    }

    private Role getRole(Member member) {
        if (this.roles.indexOf(member.getRole()) == this.roles.size() - 1) {
            return member.getRole();
        }
        return this.roles.get(this.roles.indexOf(member.getRole()) + 1);
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
        OfflinePlayer target = CC.getPlayer(args[0]);
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (!team.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (!team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.NOT_IN_TEAM").replaceAll("%player%", target.getName()));
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.DEMOTE_SELF"));
            return;
        }
        Member member = team.getMember(target.getUniqueId());
        Role role = this.getRole(member);
        if (member.getRole() == role) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.LOWEST_ROLE"));
            return;
        }
        if (!team.checkRole(player, member.getRole())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.HIGHER_ROLE"));
            return;
        }
        member.setRole(role);
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.DEMOTED_BROADCAST").replaceAll("%player%", target.getName()).replaceAll("%role%", role.getName()));
    }
}
