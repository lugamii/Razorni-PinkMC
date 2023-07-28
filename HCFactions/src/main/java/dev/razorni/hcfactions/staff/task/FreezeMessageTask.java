package dev.razorni.hcfactions.staff.task;

import dev.razorni.hcfactions.staff.StaffManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeMessageTask extends BukkitRunnable {
    private final StaffManager manager;
    private final Player player;

    public FreezeMessageTask(StaffManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.runTaskTimer(manager.getInstance(), 0L, 20L * manager.getConfig().getInt("STAFF_MODE.FREEZE_MESSAGE_INTERVAL"));
    }

    public void run() {
        if (!this.player.isOnline()) {
            this.cancel();
            return;
        }
        for (String s : this.manager.getLanguageConfig().getStringList("STAFF_MODE.VANISH_INTERVAL_MESSAGE")) {
            this.player.sendMessage(s);
        }
    }
}