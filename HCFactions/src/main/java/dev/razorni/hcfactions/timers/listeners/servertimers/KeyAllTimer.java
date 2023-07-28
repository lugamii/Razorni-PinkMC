package dev.razorni.hcfactions.timers.listeners.servertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.CustomTimer;
import dev.razorni.hcfactions.utils.Formatter;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;

public class KeyAllTimer extends CustomTimer {
    private final String command;

    public KeyAllTimer(TimerManager manager, String name, String text, long time, String command) {
        super(manager, name, text, time);
        this.command = command;
    }

    @Override
    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.getManager().getCustomTimers().remove(this.name);
            Tasks.execute(this.getManager(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command));
        }
        return Formatter.formatMMSS(time);
    }
}