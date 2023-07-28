package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TLCommand extends Command {
    public TLCommand(CommandManager manager) {
        super(manager, "tl");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("telllocation", "tellloc");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        team.broadcast(this.getLanguageConfig().getString("TELL_LOC_COMMAND.FORMAT").replaceAll("%player%", player.getName()).replaceAll("%location%", Utils.formatLocation(player.getLocation())));
    }
}
