package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamCreateArg extends Argument {
    private final Cooldown createCooldown;

    public TeamCreateArg(CommandManager manager) {
        super(manager, Collections.singletonList("create"));
        this.createCooldown = new Cooldown(manager);
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
        String name = args[0];
        Player player = (Player) sender;
        if (this.getInstance().getTeamManager().getByPlayer(player.getUniqueId()) != null) {
            this.sendMessage(sender, Config.ALREADY_IN_TEAM);
            return;
        }
        if (this.getInstance().getTeamManager().getTeam(name) != null) {
            this.sendMessage(sender, Config.TEAM_ALREADY_EXISTS.replaceAll("%team%", name));
            return;
        }
        if (Utils.isNotAlphanumeric(name)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.NOT_ALPHANUMERICAL"));
            return;
        }
        if (name.length() < Config.TEAM_NAME_MIN_LENGTH) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.MIN_LENGTH").replaceAll("%amount%", String.valueOf(Config.TEAM_NAME_MIN_LENGTH)));
            return;
        }
        if (name.length() > Config.TEAM_NAME_MAX_LENGTH) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.MAX_LENGTH").replaceAll("%amount%", String.valueOf(Config.TEAM_NAME_MAX_LENGTH)));
            return;
        }
        if (this.createCooldown.hasCooldown(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.CREATE_COOLDOWN").replaceAll("%seconds%", this.createCooldown.getRemaining(player)));
            return;
        }
        this.getInstance().getTeamManager().createTeam(name, player).save();
        this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.CREATED"));
        this.getInstance().getNametagManager().update();
        this.createCooldown.applyCooldown(player, this.getConfig().getInt("TIMERS_COOLDOWN.TEAM_CREATE_CD"));
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.CREATED_BROADCAST").replaceAll("%team%", name).replaceAll("%player%", HCF.getPlugin().getRankManager().getRankColor(player) + player.getName()));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CREATE.USAGE");
    }
}
