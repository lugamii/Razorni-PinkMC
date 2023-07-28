package dev.razorni.hcfactions.utils.extra;

import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Formatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamCooldown {

    private final Map<UUID, Long> cooldowns;

    public TeamCooldown() {
        this.cooldowns = new HashMap<>();
    }

    public void removeCooldown(PlayerTeam playerteam) {
        this.cooldowns.remove(playerteam.getUniqueID());
    }

    public void applyCooldown(PlayerTeam playerteam, int time) {
        this.cooldowns.put(playerteam.getUniqueID(), System.currentTimeMillis() + time * 1000L);
    }

    public boolean hasCooldown(PlayerTeam playerteam) {
        return this.cooldowns.containsKey(playerteam.getUniqueID()) && this.cooldowns.get(playerteam.getUniqueID()) >= System.currentTimeMillis();
    }

    public String getRemaining(PlayerTeam playerteam) {
        long time = this.cooldowns.get(playerteam.getUniqueID()) - System.currentTimeMillis();
        return Formatter.getRemaining(time, true);
    }
}
