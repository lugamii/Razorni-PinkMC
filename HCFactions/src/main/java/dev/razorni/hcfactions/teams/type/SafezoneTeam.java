package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class SafezoneTeam extends Team {
    public SafezoneTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), false, TeamType.SAFEZONE);
    }

    public SafezoneTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, false, TeamType.SAFEZONE);
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.SAFEZONE") + super.getDisplayName(player);
    }
}
