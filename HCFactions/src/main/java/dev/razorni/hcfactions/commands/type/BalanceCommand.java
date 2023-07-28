package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BalanceCommand extends Command {
    public BalanceCommand(CommandManager manager) {
        super(manager, "balance");
        this.setAsync(true);
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("BALANCE_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("eco", "bal", "$");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int balance = this.getInstance().getBalanceManager().getBalance(player.getUniqueId());
                this.sendMessage(sender, this.getLanguageConfig().getString("BALANCE_COMMAND.SELF_CHECK").replaceAll("%balance%", String.valueOf(balance)));
                return;
            }
            this.sendUsage(sender);
        } else {
            OfflinePlayer target = CC.getPlayer(args[0]);
            if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            int balance = this.getInstance().getBalanceManager().getBalance(target.getUniqueId());
            this.sendMessage(sender, this.getLanguageConfig().getString("BALANCE_COMMAND.TARGET_CHECK").replaceAll("%target%", target.getName()).replaceAll("%balance%", String.valueOf(balance)));
        }
    }
}
