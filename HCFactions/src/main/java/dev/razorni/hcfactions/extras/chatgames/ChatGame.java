package dev.razorni.hcfactions.extras.chatgames;

import dev.razorni.hcfactions.HCF;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ChatGame implements Listener {

    public boolean started;

    public ChatGame() {
        this.started = false;
        Bukkit.getPluginManager().registerEvents(this, HCF.getPlugin());
    }

    public abstract String name();

    public abstract void start();

    public abstract void end();


}
