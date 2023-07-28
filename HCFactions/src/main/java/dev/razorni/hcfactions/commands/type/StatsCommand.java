package dev.razorni.hcfactions.commands.type;

import cc.invictusgames.ilib.uuid.UUIDCache;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StatsCommand extends Command {
    public StatsCommand(CommandManager manager) {
        super(manager, "stats");
        this.setAsync(true);
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("statistics");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("STATS_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                this.sendUsage(sender);
                return;
            }
            OfflinePlayer target = CC.getPlayer(args[0]);
            User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
            if (tUser == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_TARGET")) {
                this.sendMessage(sender, s.replaceAll("%player%", UUIDCache.getName(target.getUniqueId())).replaceAll("%kills%", String.valueOf(tUser.getKills())).replaceAll("%deaths%", String.valueOf(tUser.getDeaths())).replaceAll("%kdr%", String.valueOf(tUser.getKDRString())).replaceAll("%killstreak%", String.valueOf(tUser.getKillstreak())));
            }
        } else {
            Player player = (Player) sender;
            User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
            if (args.length == 0) {
                for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_SELF")) {
                    this.sendMessage(sender, s.replaceAll("%kills%", String.valueOf(user.getKills())).replaceAll("%deaths%", String.valueOf(user.getDeaths())).replaceAll("%kdr%", String.valueOf(user.getKDRString())).replaceAll("%killstreak%", String.valueOf(user.getKillstreak())));
                }
                return;
            }
            OfflinePlayer target = CC.getPlayer(args[0]);
            User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
            if (tUser == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_TARGET")) {
                this.sendMessage(sender, s.replaceAll("%kills%", String.valueOf(tUser.getKills())).replaceAll("%deaths%", String.valueOf(tUser.getDeaths())).replaceAll("%kdr%", String.valueOf(tUser.getKDRString())).replaceAll("%killstreak%", String.valueOf(tUser.getKillstreak())));
            }
        }
    }
}
