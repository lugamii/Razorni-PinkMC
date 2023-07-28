package dev.razorni.hcfactions.timers.listeners.servertimers;

import dev.razorni.hcfactions.timers.Timer;
import dev.razorni.hcfactions.timers.TimerManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EOTWTimer extends Timer {
    private boolean active;
    private Long remaining;

    public EOTWTimer(TimerManager manager) {
        super(manager, "EOTW", "", 0);
        this.remaining = 0L;
        this.active = false;
    }
}