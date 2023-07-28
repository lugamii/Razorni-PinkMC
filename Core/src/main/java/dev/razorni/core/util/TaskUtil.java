package dev.razorni.core.util;

import dev.razorni.core.Core;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        Core.getInstance().getServer().getScheduler().runTask(Core.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Core.getInstance().getServer().getScheduler().runTaskTimer(Core.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Core.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Core.getInstance().getServer().getScheduler().runTaskLater(Core.getInstance(), runnable, delay);
    }

    public static void runAsync(Runnable runnable) {
        Core.getInstance().getServer().getScheduler().runTaskAsynchronously(Core.getInstance(), runnable);
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        Core.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Core.getInstance(), runnable, delay, timer);
    }

    public static Thread runAsyncReturn(Runnable runnable) {
        return new Thread(runnable);
    }
}
