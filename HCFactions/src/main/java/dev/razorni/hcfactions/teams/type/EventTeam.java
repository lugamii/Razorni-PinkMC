package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class EventTeam extends Team {
    public EventTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), true, TeamType.EVENT);
    }

    public EventTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.EVENT);
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.EVENT") + super.getDisplayName(player);
    }
}
