package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;

public class ArcherTagTimer extends PlayerTimer {
    public ArcherTagTimer(TimerManager manager) {
        super(manager, false, "ArcherTag", "PLAYER_TIMERS.ARCHER_TAG", manager.getConfig().getInt("TIMERS_COOLDOWN.ARCHER_TAG"));
    }
}