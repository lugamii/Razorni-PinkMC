package dev.razorni.hcfactions.listeners.type.team;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerTeamListener extends Module<TeamManager> {
    public PlayerTeamListener(TeamManager manager) {
        super(manager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            return;
        }
        team.broadcast(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.MEMBER_ONLINE").replaceAll("%player%", player.getName()));
        team.broadcastAlly(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.ALLY_ONLINE").replaceAll("%player%", player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            return;
        }
        team.broadcast(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.MEMBER_OFFLINE").replaceAll("%player%", player.getName()));
        team.broadcastAlly(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.ALLY_OFFLINE").replaceAll("%player%", player.getName()));
    }
}
