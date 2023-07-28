package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PayCommand extends Command {

    public PayCommand(CommandManager llllllllllllllllIIIIIIlIIllIIlIl) {
        super(llllllllllllllllIIIIIIlIIllIIlIl, "pay");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        Integer integer = this.getInt(args[1]);
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (integer == null || integer <= 0) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        if (user.getBalance() < integer) {
            this.sendMessage(sender, this.getLanguageConfig().getString("PAY_COMMAND.INSUFFICIENT_BAL").replaceAll("%amount%", String.valueOf(integer)));
            return;
        }
        User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        user.setBalance(user.getBalance() - integer);
        user.save();
        tUser.setBalance(tUser.getBalance() + integer);
        tUser.save();
        this.sendMessage(target, this.getLanguageConfig().getString("PAY_COMMAND.RECEIVED").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(integer)));
        this.sendMessage(sender, this.getLanguageConfig().getString("PAY_COMMAND.PAID").replaceAll("%player%", target.getName()).replaceAll("%amount%", String.valueOf(integer)));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("PAY_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
