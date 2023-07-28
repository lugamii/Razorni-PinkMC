package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GlowstoneMountainTeam extends Team {
    public GlowstoneMountainTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), true, TeamType.MOUNTAIN);
    }

    public GlowstoneMountainTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.MOUNTAIN);
    }

    @Override
    public String getDisplayName(Player player) {
        return ChatColor.GOLD + "Glowstone";
    }
}
