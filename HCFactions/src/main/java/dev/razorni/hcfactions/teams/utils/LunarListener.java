package dev.razorni.hcfactions.teams.utils;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LunarListener implements Runnable {

    @Override
    public void run() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerTeam team = HCF.getPlugin().getTeamManager().getTeam(online);
            Map<UUID, Map<String, Double>> players = new HashMap<>();
            if (team != null) {
                for (Player teammate : team.getOnlinePlayers()) {
                    if (teammate == online) continue;
                    Map<String, Double> position = new HashMap<>();
                    position.put("x", teammate.getLocation().getX());
                    position.put("y", teammate.getLocation().getY());
                    position.put("z", teammate.getLocation().getZ());
                    players.put(teammate.getUniqueId(), position);
                }
                LunarClientAPI.getInstance().sendTeammates(online, new LCPacketTeammates(team.getLeader(), System.currentTimeMillis(), players));
            } else {
                LunarClientAPI.getInstance().sendTeammates(online, new LCPacketTeammates(online.getUniqueId(), System.currentTimeMillis(), players));
            }
        }
    }
}