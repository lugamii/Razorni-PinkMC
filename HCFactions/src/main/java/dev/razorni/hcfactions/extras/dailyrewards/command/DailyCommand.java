package dev.razorni.hcfactions.extras.dailyrewards.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.dailyrewards.menu.PrizesMenu;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DailyCommand extends Command {

    public DailyCommand(CommandManager manager) {
        super(manager, "daily");
        this.setAsync(true);
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("BALANCE_COMMAND.USAGE");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("prize", "prizes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new PrizesMenu().openMenu(player);
        }
    }

}

