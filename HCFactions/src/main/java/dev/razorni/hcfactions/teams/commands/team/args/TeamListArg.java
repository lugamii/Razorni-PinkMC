package dev.razorni.hcfactions.teams.commands.team.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.extra.TeamSorting;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.users.settings.TeamListSetting;
import dev.razorni.hcfactions.utils.Formatter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.command.CommandSender;

import java.util.*;

public class TeamListArg extends Argument {
    public TeamListArg(CommandManager manager) {
        super(manager, Collections.singletonList("list"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TeamSorting sorted = this.getInstance().getTeamManager().getTeamSorting();
        TeamListSetting settings = sorted.getSetting(sender);
        Map<Integer, List<PlayerTeam>> map = new HashMap<>();
        List<PlayerTeam> teams = this.getInstance().getTeamManager().getTeamSorting().getList(sender);
        int i = 1;
        if (teams.isEmpty()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LIST.NO_TEAMS_ONLINE"));
            return;
        }
        int f = 0;
        for (int t = 0; t < teams.size(); ++t) {
            if (t % 10 == 0) {
                ++f;
            }
            PlayerTeam tTeam = teams.get(t);
            if (!map.containsKey(f)) {
                map.put(f, new ArrayList<>());
            }
            map.get(f).add(tTeam);
        }
        if (args.length > 0) {
            Integer o = this.getInt(args[0]);
            if (o == null) {
                this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
                return;
            }
            if (!map.containsKey(o)) {
                this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LIST.PAGE_NOT_FOUND").replaceAll("%number%", args[0]));
                return;
            }
            i = o;
        }
        List<String> lines = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_LIST.LIST_SHOWN");
        for (String s : lines) {
            if (!s.equalsIgnoreCase("%team_list%")) {
                this.sendMessage(sender, s.replaceAll("%page%", String.valueOf(i)).replaceAll("%max-pages%", String.valueOf(f)));
            } else {
                List<PlayerTeam> iTeam = map.get(i);
                for (int j = 0; j < iTeam.size(); ++j) {
                    PlayerTeam playerTeam = iTeam.get(j);
                    String str;
                    if (settings.name().contains("DTR")) {
                        str = this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LIST.FORMAT_DTR").replaceAll("%team%", playerTeam.getName()).replaceAll("%dtr%", playerTeam.getDtrString()).replaceAll("%max-dtr%", Formatter.formatDtr(playerTeam.getMaxDtr()));
                    } else {
                        str = this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LIST.FORMAT_ONLINE").replaceAll("%team%", playerTeam.getName()).replaceAll("%online%", String.valueOf(playerTeam.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(playerTeam.getPlayers().size()));
                    }
                    List<String> list = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_LIST.HOVER_MESSAGE");
                    list.replaceAll(g -> g.replaceAll("%dtr%", playerTeam.getDtrString()).replaceAll("%hq%", playerTeam.getHQFormatted()));
                    int pos = (i - 1) * 10 + j;
                    new FancyMessage(str.replaceAll("%number%", String.valueOf(pos + 1))).command("/team info " + playerTeam.getName()).tooltip(list).send(sender);
                }
            }
        }
    }

    @Override
    public String usage() {
        return null;
    }
}
