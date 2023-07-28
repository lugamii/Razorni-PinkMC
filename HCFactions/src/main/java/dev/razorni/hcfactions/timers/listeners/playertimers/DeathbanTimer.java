package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DeathbanTimer extends PlayerTimer {
    private final Map<String, Long> deathbanTime;

    public DeathbanTimer(TimerManager manager) {
        super(manager, false, "Deathban", "", 0);
        this.deathbanTime = new HashMap<>();
        this.load();
    }
    
    /*//This shit dont use att UKry :)
    @EventHandler
    public void onExpire(TimerExpireEvent evnet) {
        if (!(evnet.getTimer() instanceof DeathbanTimer)) {
            return;
        }
    }*/

    @Override
    public void applyTimer(Player player) {
        if (player.hasPermission("azurite.deathban.bypass")) {
            return;
        }
        this.timerCache.put(player.getUniqueId(), System.currentTimeMillis() + this.getDeathbanTime(player));
    }

    private long getDeathbanTime(Player player) {
        long time = this.getConfig().getInt("DEATHBANS.DEFAULT_TIME") * 60000L;
        for (Map.Entry<String, Long> map : this.deathbanTime.entrySet()) {
            String permission = map.getKey();
            Long value = map.getValue();
            if (player.hasPermission(permission) && value < time) {
                time = value;
            }
        }
        return time;
    }

    private void load() {
        for (String s : this.getConfig().getStringList("DEATHBANS.TIMES")) {
            String[] deathbans = s.split(", ");
            this.deathbanTime.put("azurite.deathban." + deathbans[0].toLowerCase(), Integer.parseInt(deathbans[1]) * 60000L);
        }
    }
}
