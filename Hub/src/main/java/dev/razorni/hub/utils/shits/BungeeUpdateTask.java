package dev.razorni.hub.utils.shits;

import dev.razorni.hub.utils.BungeeListener;
import org.bukkit.Bukkit;

public class BungeeUpdateTask implements Runnable {
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(BungeeListener::updateCount);
    }
}
