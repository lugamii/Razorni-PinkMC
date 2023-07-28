package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamFocusArg extends Argument {
    public TeamFocusArg(CommandManager manager) {
        super(manager, Collections.singletonList("focus"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getStringTeams().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
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
        PlayerTeam from = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        PlayerTeam to = this.getInstance().getTeamManager().getByPlayerOrTeam(args[0]);
        if (from == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (to == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (from.getAllies().contains(to.getUniqueID())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_FOCUS.FOCUS_ALLY"));
            return;
        }
        if (from == to) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_FOCUS.FOCUS_SELF"));
            return;
        }
        if (from.getFocus() != null && from.getFocus() == to.getUniqueID()) {
            for (Player online : from.getOnlinePlayers()) {
                this.getInstance().getWaypointManager().getFocusWaypoint().remove(online, to.getHq(), lllllllllllllllllIIlIIIlllIlIIII -> lllllllllllllllllIIlIIIlllIlIIII.replaceAll("%team%", to.getName()));
            }
            from.setFocus(null);
            from.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.FOCUS_CLEARED"));
            this.getInstance().getNametagManager().update();
            return;
        }
        for (Player online : from.getOnlinePlayers()) {
            this.getInstance().getWaypointManager().getFocusWaypoint().send(online, to.getHq(), lllllllllllllllllIIlIIIlllIlIllI -> lllllllllllllllllIIlIIIlllIlIllI.replaceAll("%team%", to.getName()));
        }
        from.setFocus(to.getUniqueID());
        from.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_FOCUS.FOCUS_UPDATED").replaceAll("%team%", to.getName()));
        this.getInstance().getNametagManager().update();
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_FOCUS.USAGE");
    }
}
