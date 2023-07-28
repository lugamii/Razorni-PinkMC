package dev.razorni.hcfactions.utils.tablist.thread;

import dev.razorni.hcfactions.utils.tablist.Tablist;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistThread extends Thread {
    private final TablistManager manager;

    public TablistThread(TablistManager manager) {
        super("Azurite - TablistThread");
        this.manager = manager;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Tablist tablist = this.manager.getTablists().get(online.getUniqueId());
                    if (tablist != null) {
                        tablist.update();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
