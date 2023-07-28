package dev.razorni.hcfactions.extras.workload;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class ScheduleWorkLoad extends BukkitRunnable {

    public abstract void compute();

    public abstract boolean isFinished();

    public abstract void pause();

    public abstract void resume();

}
