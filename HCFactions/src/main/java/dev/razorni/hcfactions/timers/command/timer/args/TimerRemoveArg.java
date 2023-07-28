package dev.razorni.hcfactions.timers.command.timer.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TimerRemoveArg extends Argument {
    public TimerRemoveArg(CommandManager manager) {
        super(manager, Collections.singletonList("remove"));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_REMOVE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[0]);
            PlayerTimer timer = this.getInstance().getTimerManager().getPlayerTimer(args[1]);
            if (timer == null) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TIMER_COMMAND.NOT_FOUND").replaceAll("%timer%", args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!timer.hasTimer(online)) {
                        continue;
                    }
                    timer.removeTimer(online);
                }
                this.getInstance().getNametagManager().update();
                sender.sendMessage(this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_REMOVE.REMOVED_TIMER_ALL").replaceAll("%timer%", timer.getName()));
                return;
            }
            if (player == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replace("%player%", args[0]));
                return;
            }
            if (!timer.hasTimer(player)) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_REMOVE.NO_TIMER").replaceAll("%player%", args[0]).replaceAll("%timer%", args[1]));
                return;
            }
            timer.removeTimer(player);
            this.getInstance().getNametagManager().update();
            this.sendMessage(sender, this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_REMOVE.REMOVED_TIMER").replaceAll("%player%", args[0]).replaceAll("%timer%", args[1]));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            String other = args[args.length - 1];
            return this.getInstance().getTimerManager().getPlayerTimers().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}