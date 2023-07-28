package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamMapArg extends Argument {
    public TeamMapArg(CommandManager manager) {
        super(manager, Arrays.asList("map", "claims"));
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (user.isClaimsShown()) {
            user.setClaimsShown(false);
            this.getInstance().getWallManager().clearTeamMap(player);
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.MAP_HIDDEN"));
            return;
        }
        user.setClaimsShown(true);
        this.getInstance().getWallManager().sendTeamMap(player);
    }
}
