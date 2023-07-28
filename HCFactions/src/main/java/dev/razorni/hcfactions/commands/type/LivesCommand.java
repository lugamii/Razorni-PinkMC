package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LivesCommand extends Command {
    public LivesCommand(CommandManager manager) {
        super(manager, "lives");
        this.completions.add(new TabCompletion(Arrays.asList("revive", "send", "give", "check"), 0));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("live");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (args.length == 0) {
            this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.SELF_CHECK").replaceAll("%lives%", String.valueOf(user.getLives())));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "revive": {
                if (args.length < 2) {
                    this.sendUsage(sender);
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                if (!this.getInstance().getDeathbanManager().isDeathbanned(target)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.REVIVE_NOT_DEATHBANNED"));
                    return;
                }
                if (user.getLives() <= 0) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.REVIVE_NO_LIVES"));
                    return;
                }
                user.setLives(user.getLives() - 1);
                user.save();
                this.getInstance().getDeathbanManager().removeDeathban(target);
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.REVIVED").replaceAll("%player%", target.getName()));
                return;
            }
            case "check": {
                if (args.length < 2) {
                    this.sendUsage(sender);
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.OTHER_CHECK").replaceAll("%player%", target.getName()).replaceAll("%lives%", String.valueOf(tUser.getLives())));
                return;
            }
            case "send":
            case "give": {
                if (args.length < 3) {
                    this.sendUsage(sender);
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                Integer lives = this.getInt(args[2]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                if (lives == null || lives <= 0) {
                    this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
                    return;
                }
                if (user.getLives() < lives) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.INSUFFICIENT_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%lives%", String.valueOf(user.getLives())));
                    return;
                }
                User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
                user.setLives(user.getLives() - lives);
                user.save();
                tUser.setLives(tUser.getLives() + lives);
                tUser.save();
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVES_COMMAND.GAVE_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%player%", target.getName()));
                this.sendMessage(target, this.getLanguageConfig().getString("LIVES_COMMAND.RECEIVED_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%player%", player.getName()));
                return;
            }
        }
        this.sendUsage(sender);
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("LIVES_COMMAND.USAGE");
    }
}
