package dev.razorni.hcfactions.timers.event;

import dev.razorni.hcfactions.timers.type.PlayerTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
@Setter
public class TimerExpireEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private UUID player;
    private PlayerTimer timer;

    public TimerExpireEvent(PlayerTimer timer, UUID player) {
        super(true);
        this.timer = timer;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return TimerExpireEvent.handlers;
    }

    public HandlerList getHandlers() {
        return TimerExpireEvent.handlers;
    }
}
