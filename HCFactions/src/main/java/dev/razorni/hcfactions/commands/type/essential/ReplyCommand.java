package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReplyCommand extends Command {
    public ReplyCommand(CommandManager manager) {
        super(manager, "reply");
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
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        String text = String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, 0, args.length));
        if (user.getReplied() == null || Bukkit.getPlayer(user.getReplied()) == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPLY_COMMAND.NO_REPLY"));
            return;
        }
        Player target = Bukkit.getPlayer(user.getReplied());
        User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (user.getIgnoring().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPLY_COMMAND.IGNORING_PLAYER"));
            return;
        }
        if (tUser.getIgnoring().contains(player.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPLY_COMMAND.IGNORING_TARGET"));
            return;
        }
        if (!user.isPrivateMessages()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPLY_COMMAND.TOGGLED_PLAYER"));
            return;
        }
        if (!tUser.isPrivateMessages()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPLY_COMMAND.TOGGLED_TARGET"));
            return;
        }
        target.sendMessage(this.getLanguageConfig().getString("MESSAGE_COMMAND.FROM_FORMAT").replaceAll("%player%", player.getName()).replaceAll("%message%", text).replaceAll("%prefix%", CC.t(this.getInstance().getRankManager().getRankPrefix(player))).replaceAll("%suffix%", CC.t(this.getInstance().getRankManager().getRankSuffix(player))).replaceAll("%color%", CC.t(this.getInstance().getRankManager().getRankColor(player))));
        player.sendMessage(this.getLanguageConfig().getString("MESSAGE_COMMAND.TO_FORMAT").replaceAll("%player%", target.getName()).replaceAll("%message%", text).replaceAll("%prefix%", CC.t(this.getInstance().getRankManager().getRankPrefix(target))).replaceAll("%suffix%", CC.t(this.getInstance().getRankManager().getRankSuffix(target))).replaceAll("%color%", CC.t(this.getInstance().getRankManager().getRankColor(target))));
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("REPLY_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("r", "w");
    }
}
