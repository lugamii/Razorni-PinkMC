package dev.razorni.hcfactions.events.king;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.events.king.listener.KingListener;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.Formatter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class KingManager extends Manager {
    private Player king;
    private long startedTime;
    private String reward;

    public KingManager(HCF plugin) {
        super(plugin);
        this.king = null;
        this.reward = null;
        this.startedTime = 0L;
        new KingListener(this);
    }

    public void startKing(Player player, String reward) {
        this.king = player;
        this.reward = reward;
        this.startedTime = System.currentTimeMillis();
        this.getInstance().getKitManager().getKit("ktk").equip(player);
        for (String s : this.getLanguageConfig().getStringList("KING_EVENTS.BROADCAST_START")) {
            Bukkit.broadcastMessage(s.replaceAll("%player%", this.king.getName()).replaceAll("%reward%", reward));
        }
    }

    public void stopKing(boolean killed) {
        String killer = (this.king.getKiller() == null) ? "Unknown" : this.king.getKiller().getName();
        long time = System.currentTimeMillis() - this.startedTime;
        for (String s : this.getLanguageConfig().getStringList("KING_EVENTS.BROADCAST_" + (killed ? "END" : "KILL"))) {
            Bukkit.broadcastMessage(s.replaceAll("%player%", this.king.getName()).replaceAll("%reward%", this.reward).replaceAll("%killer%", killer).replaceAll("%time%", Formatter.formatDetailed(time)));
        }
        this.king = null;
        this.reward = null;
        this.startedTime = 0L;
    }

    public boolean isActive() {
        return this.king != null;
    }
}
