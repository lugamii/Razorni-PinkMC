package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.timers.listeners.playertimers.GappleTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GappleCommand extends Command {
    public GappleCommand(CommandManager manager) {
        super(manager, "gapple");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        GappleTimer timer = this.getInstance().getTimerManager().getGappleTimer();
        if (!timer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("GAPPLE_COMMAND.NO_TIMER"));
            return;
        }
        this.sendMessage(sender, this.getLanguageConfig().getString("GAPPLE_COMMAND.FORMAT").replaceAll("%remaining%", timer.getRemainingString(player)));
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("gopple");
    }
}
