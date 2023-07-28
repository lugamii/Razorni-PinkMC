package dev.razorni.hcfactions.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.razorni.hcfactions.extras.framework.Manager;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.concurrent.ThreadFactory;

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

    public static void runAsync(final Plugin plugin, final Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void executeScheduledAsync(Manager manager, int HCF, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(manager.getInstance(), runnable, 0L, HCF);
    }

    public static void execute(Manager manager, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTask(manager.getInstance(), runnable);
    }

    public static void runLater(final Plugin plugin, final Runnable runnable, final long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static ThreadFactory newThreadFactory(String name, Thread.UncaughtExceptionHandler handler) {
        return new ThreadFactoryBuilder().setNameFormat(name).setUncaughtExceptionHandler(handler).build();
    }
}
