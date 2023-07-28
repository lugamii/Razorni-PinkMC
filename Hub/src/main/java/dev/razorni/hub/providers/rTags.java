package dev.razorni.hub.providers;

import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.shits.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class rTags {

    public rTags() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
                    Team team = board.getTeam(player.getName());
                    if (team == null) {
                        team = board.registerNewTeam(player.getName());
                    }
                    String prime = "";
                    if (Profile.getByUuid(player.getUniqueId()).isPrime()) {
                        prime = CC.translate(" &6✪");
                    }
                    team.setPrefix(String.valueOf(Core.getInstance().getCoreAPI().getRankColor(player)));
                    team.setSuffix(prime);
                    team.addPlayer(player);
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.setScoreboard(board);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Hub.getInstance(), 20, 40);
    }


}
