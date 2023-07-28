package dev.razorni.hcfactions.teams.type;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WarzoneTeam extends Team {
    private final Map<Location, BukkitTask> warzoneCobwebs;

    public WarzoneTeam(TeamManager manager) {
        super(manager, "Warzone", UUID.randomUUID(), true, TeamType.WARZONE);
        this.warzoneCobwebs = new ConcurrentHashMap<>();
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.WARZONE") + super.getDisplayName(player);
    }

    public boolean canBreak(Location location) {
        int x = Math.abs(location.getBlockX());
        int z = Math.abs(location.getBlockZ());
        int warzoneBreak = this.getTeamConfig().getInt("WARZONE.WARZONE_BREAK");
        int netherBreak = this.getTeamConfig().getInt("WARZONE.WARZONE_BREAK_NETHER");
        if (location.getWorld().getEnvironment() == World.Environment.NORMAL) {
            return x > warzoneBreak || z > warzoneBreak;
        }
        return location.getWorld().getEnvironment() == World.Environment.NETHER && (x > netherBreak || z > netherBreak);
    }

    public boolean canInteract(Location location) {
        Block block = location.getBlock();
        return block.getType() == Material.WOOD_PLATE;
    }

    public void clearWebs() {
        for (Location location : this.warzoneCobwebs.keySet()) {
            location.getBlock().setType(Material.AIR);
        }
    }
}