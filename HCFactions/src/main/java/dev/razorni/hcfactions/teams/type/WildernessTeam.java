package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WildernessTeam extends Team {
    public WildernessTeam(TeamManager manager) {
        super(manager, "Wilderness", UUID.randomUUID(), true, TeamType.WILDERNESS);
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.WILDERNESS") + super.getDisplayName(player);
    }
}
