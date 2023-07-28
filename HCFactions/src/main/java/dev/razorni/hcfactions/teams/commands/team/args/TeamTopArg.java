package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamTopArg extends Argument {
    public TeamTopArg(CommandManager manager) {
        super(manager, Collections.singletonList("top"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<Integer, List<PlayerTeam>> map = new HashMap<>();
        List<PlayerTeam> teams = this.getInstance().getTeamManager().getTeamSorting().getTeamTop();
        int pos = 1;
        if (teams.isEmpty()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_TOP.NO_TEAMS_ONLINE"));
            return;
        }
        int i = 0;
        for (int f = 0; f < teams.size(); ++f) {
            if (f % 10 == 0) {
                ++i;
            }
            PlayerTeam targetTeam = teams.get(f);
            if (!map.containsKey(i)) {
                map.put(i, new ArrayList<>());
            }
            map.get(i).add(targetTeam);
        }
        if (args.length > 0) {
            Integer j = this.getInt(args[0]);
            if (j == null) {
                this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
                return;
            }
            if (!map.containsKey(j)) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_TOP.PAGE_NOT_FOUND").replaceAll("%number%", args[0]));
                return;
            }
            pos = j;
        }
        List<String> lines = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_TOP.TOP_SHOWN");
        for (String s : lines) {
            if (!s.equals("%team_top%")) {
                this.sendMessage(sender, s.replaceAll("%page%", String.valueOf(pos)).replaceAll("%max-pages%", String.valueOf(i)));
            } else {
                List<PlayerTeam> tops = map.get(pos);
                for (int j = 0; j < tops.size(); ++j) {
                    PlayerTeam topTeam = tops.get(j);
                    String format = this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_TOP.FORMAT_TEAM").replaceAll("%team%", (sender instanceof Player) ? topTeam.getDisplayName((Player) sender) : topTeam.getName()).replaceAll("%points%", String.valueOf(topTeam.getPoints()));
                    List<String> texts = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_TOP.HOVER_MESSAGE");
                    texts.replaceAll(f -> f.replaceAll("%team%", (sender instanceof Player) ? topTeam.getDisplayName((Player) sender) : topTeam.getName()).replaceAll("%leader%", Bukkit.getOfflinePlayer(topTeam.getLeader()).getName()).replaceAll("%balance%", String.valueOf(topTeam.getBalance())).replaceAll("%kills%", String.valueOf(topTeam.getKills())).replaceAll("%deaths%", String.valueOf(topTeam.getDeaths())).replaceAll("%captures%", String.valueOf(topTeam.getKothCaptures())));
                    int fTop = (pos - 1) * 10 + j;
                    new FancyMessage(format.replaceAll("%number%", String.valueOf(fTop + 1))).command("/team info " + topTeam.getName()).tooltip(texts).send(sender);
                }
            }
        }
        map.clear();
    }

    @Override
    public String usage() {
        return null;
    }
}
