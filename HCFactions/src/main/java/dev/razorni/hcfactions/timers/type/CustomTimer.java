package dev.razorni.hcfactions.timers.type;

import dev.razorni.hcfactions.timers.Timer;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.utils.Formatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomTimer extends Timer {
    protected Long remaining;
    protected String displayName;

    public CustomTimer(TimerManager manager, String name, String text, long time) {
        super(manager, name, "", 0);
        this.displayName = text;
        this.remaining = System.currentTimeMillis() + time;
        this.getManager().getCustomTimers().put(name, this);
    }

    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.getManager().getCustomTimers().remove(this.name);
        }
        return Formatter.formatMMSS(time);
    }
}