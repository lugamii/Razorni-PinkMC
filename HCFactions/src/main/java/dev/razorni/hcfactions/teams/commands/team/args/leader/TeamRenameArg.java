package dev.razorni.hcfactions.teams.commands.team.args.leader;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamRenameArg extends Argument {
    private final Cooldown renameCooldown;

    public TeamRenameArg(CommandManager manager) {
        super(manager, Collections.singletonList("rename"));
        this.renameCooldown = new Cooldown(manager);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.USAGE");
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
        String name = args[0];
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (team.getName().equals(name)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.ALREADY_NAME").replaceAll("%name%", name));
            return;
        }
        if (this.getInstance().getTeamManager().getPlayerTeam(name) != null) {
            this.sendMessage(sender, Config.TEAM_ALREADY_EXISTS.replaceAll("%team%", name));
            return;
        }
        if (Utils.isNotAlphanumeric(name)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.NOT_ALPHANUMERICAL"));
            return;
        }
        if (name.length() < Config.TEAM_NAME_MIN_LENGTH) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.MIN_LENGTH").replaceAll("%amount%", String.valueOf(Config.TEAM_NAME_MIN_LENGTH)));
            return;
        }
        if (name.length() > Config.TEAM_NAME_MAX_LENGTH) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.MAX_LENGTH").replaceAll("%amount%", String.valueOf(Config.TEAM_NAME_MAX_LENGTH)));
            return;
        }
        if (this.renameCooldown.hasCooldown(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.RENAME_COOLDOWN").replaceAll("%seconds%", this.renameCooldown.getRemaining(player)));
            return;
        }
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RENAME.RENAMED").replaceAll("%team%", team.getName()).replaceAll("%name%", name));
        this.getInstance().getTeamManager().getStringTeams().remove(team.getName());
        this.getInstance().getTeamManager().getStringTeams().put(name, team);
        this.renameCooldown.applyCooldown(player, this.getConfig().getInt("TIMERS_COOLDOWN.TEAM_RENAME_CD"));
        team.setName(name);
        team.save();
        this.getInstance().getNametagManager().update();
    }
}
