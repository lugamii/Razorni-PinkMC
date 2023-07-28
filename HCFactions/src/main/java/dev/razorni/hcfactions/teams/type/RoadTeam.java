package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class RoadTeam extends Team {
    public RoadTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.ROAD);
    }

    public RoadTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), true, TeamType.ROAD);
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.ROADS") + super.getDisplayName(player);
    }
}
