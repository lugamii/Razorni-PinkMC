package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.menus.TeamSortMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamSortArg extends Argument {
    public TeamSortArg(CommandManager manager) {
        super(manager, Arrays.asList("sort", "filter"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        new TeamSortMenu(this.getInstance().getMenuManager(), (Player) sender).open();
    }

    @Override
    public String usage() {
        return null;
    }
}
