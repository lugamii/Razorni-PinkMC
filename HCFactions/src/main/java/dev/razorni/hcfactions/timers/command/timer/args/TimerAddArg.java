package dev.razorni.hcfactions.timers.command.timer.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TimerAddArg extends Argument {
    public TimerAddArg(CommandManager manager) {
        super(manager, Collections.singletonList("add"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            String other = args[args.length - 1];
            return this.getInstance().getTimerManager().getPlayerTimers().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_ADD.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            this.sendUsage(sender);
            return;
        }
        if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[0]);
            PlayerTimer timer = this.getInstance().getTimerManager().getPlayerTimer(args[1]);
            Long time = Formatter.parse(args[2]);
            if (timer == null) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TIMER_COMMAND.NOT_FOUND").replaceAll("%timer%", args[1]));
                return;
            }
            if (time == null) {
                this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    timer.applyTimer(online, time);
                }
                sender.sendMessage(this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_ADD.ADDED_TIMER_ALL").replaceAll("%timer%", timer.getName()).replaceAll("%time%", Formatter.getRemaining(time, true)));
                return;
            }
            if (player == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            timer.applyTimer(player, time);
            this.getInstance().getNametagManager().update();
            this.sendMessage(sender, this.getLanguageConfig().getString("TIMER_COMMAND.TIMER_ADD.ADDED_TIMER").replaceAll("%timer%", timer.getName()).replaceAll("%player%", player.getName()).replaceAll("%time%", Formatter.getRemaining(time, true)));
        }
    }
}
