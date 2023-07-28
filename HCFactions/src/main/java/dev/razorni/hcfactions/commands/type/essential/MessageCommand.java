package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MessageCommand extends Command {
    public MessageCommand(CommandManager manager) {
        super(manager, "message");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("msg", "tell");
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
        Player target = Bukkit.getPlayer(args[0]);
        String text = String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, 1, args.length));
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        User pUser = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (pUser.getIgnoring().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("MESSAGE_COMMAND.IGNORING_TARGET"));
            return;
        }
        if (tUser.getIgnoring().contains(player.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("MESSAGE_COMMAND.IGNORING_PLAYER"));
            return;
        }
        if (!pUser.isPrivateMessages()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("MESSAGE_COMMAND.TOGGLED_TARGET"));
            return;
        }
        if (!tUser.isPrivateMessages()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("MESSAGE_COMMAND.TOGGLED_PLAYER"));
            return;
        }
        target.sendMessage(this.getLanguageConfig().getString("MESSAGE_COMMAND.FROM_FORMAT").replaceAll("%player%", player.getName()).replaceAll("%message%", text).replaceAll("%prefix%", CC.t(this.getInstance().getRankManager().getRankPrefix(player))).replaceAll("%suffix%", CC.t(this.getInstance().getRankManager().getRankSuffix(player))).replaceAll("%color%", CC.t(this.getInstance().getRankManager().getRankColor(player))));
        player.sendMessage(this.getLanguageConfig().getString("MESSAGE_COMMAND.TO_FORMAT").replaceAll("%player%", target.getName()).replaceAll("%message%", text).replaceAll("%prefix%", CC.t(this.getInstance().getRankManager().getRankPrefix(target))).replaceAll("%suffix%", CC.t(this.getInstance().getRankManager().getRankSuffix(target))).replaceAll("%color%", CC.t(this.getInstance().getRankManager().getRankColor(target))));
        if (tUser.isPrivateMessagesSound()) {
            target.playSound(target.getLocation(), Sound.valueOf(this.getConfig().getString("MESSAGE_SOUND")), 1.0f, 1.0f);
        }
        pUser.setReplied(target.getUniqueId());
        tUser.setReplied(player.getUniqueId());
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("MESSAGE_COMMAND.USAGE");
    }
}
