package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamDepositArg extends Argument {
    public TeamDepositArg(CommandManager manager) {
        super(manager, Arrays.asList("deposit", "d"));
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
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        int balance = this.getInstance().getBalanceManager().getBalance(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            if (balance <= 0) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSIT_ZERO"));
                return;
            }
            team.setBalance(team.getBalance() + balance);
            team.save();
            this.getInstance().getBalanceManager().setBalance(player, 0);
            team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSITED").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(balance)));
        } else {
            Integer amount = this.getInt(args[0]);
            if (amount == null) {
                this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
                return;
            }
            if (balance < amount) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEPOSIT.INSUFFICIENT_BAL").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%balance%", String.valueOf(balance)));
                return;
            }
            this.getInstance().getBalanceManager().takeBalance(player, amount);
            team.setBalance(team.getBalance() + amount);
            team.save();
            team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSITED").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(amount)));
        }
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEPOSIT.USAGE");
    }
}
