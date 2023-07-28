package dev.razorni.hub.utils.shits;

import dev.razorni.hub.framework.Manager;
import org.bukkit.Bukkit;

public class Tasks {

    public static void executeScheduled(Manager manager, int HCF, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskTimer(manager.getInstance(), runnable, 0L, HCF);
    }

    public static void executeLater(Manager manager, int HCF, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskLater(manager.getInstance(), runnable, HCF);
    }

    public static void executeAsync(Manager manager, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(manager.getInstance(), runnable);
    }

    public static void executeScheduledAsync(Manager manager, int HCF, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(manager.getInstance(), runnable, 0L, HCF);
    }

    public static void execute(Manager manager, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTask(manager.getInstance(), runnable);
    }
}
