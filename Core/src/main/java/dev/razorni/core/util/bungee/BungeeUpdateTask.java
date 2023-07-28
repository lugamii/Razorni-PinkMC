package dev.razorni.core.util.bungee;

import org.bukkit.Bukkit;

public class BungeeUpdateTask implements Runnable {
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(BungeeListener::updateCount);
    }
}
