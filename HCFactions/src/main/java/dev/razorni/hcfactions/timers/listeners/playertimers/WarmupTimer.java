package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarmupTimer extends PlayerTimer {
    private final Map<UUID, PvPClass> warmups;

    public WarmupTimer(TimerManager manager) {
        super(manager, false, "Warmup", "PLAYER_TIMERS.WARMUP", manager.getConfig().getInt("TIMERS_COOLDOWN.WARMUP"));
        this.warmups = new HashMap<>();
    }

    public void putTimerWithClass(Player player, PvPClass pvpClass) {
        super.applyTimer(player);
        this.warmups.put(player.getUniqueId(), pvpClass);
    }

    public Map<UUID, PvPClass> getWarmups() {
        return this.warmups;
    }

    @Override
    public void removeTimer(Player player) {
        super.removeTimer(player);
        this.warmups.remove(player.getUniqueId());
    }
}