package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.timers.listeners.playertimers.LogoutTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LogoutCommand extends Command {

    public LogoutCommand(CommandManager commandManager) {
        super(commandManager, "logout");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        LogoutTimer logoutTimer = this.getInstance().getTimerManager().getLogoutTimer();
        if (logoutTimer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("LOGOUT_COMMAND.ALREADY_ACTIVE"));
            return;
        }
        if (this.getConfig().getBoolean("COMBAT_TIMER.LOGOUT_COMMAND") && this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("LOGOUT_COMMAND.COMBAT_TAGGED"));
            return;
        }
        logoutTimer.applyTimer(player);
        this.sendMessage(player, this.getLanguageConfig().getString("LOGOUT_COMMAND.STARTED_LOGOUT").replaceAll("%seconds%", String.valueOf(logoutTimer.getSeconds())));
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
