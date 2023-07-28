package dev.razorni.hcfactions.timers;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.CC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer extends Module<TimerManager> {
    protected int seconds;
    protected String name;
    protected String scoreboardPath;

    public Timer(TimerManager manager, String name, String text, int seconds) {
        super(manager);
        this.name = name;
        this.scoreboardPath = CC.t(text);
        this.seconds = seconds;
    }
}