package dev.razorni.hcfactions.pvpclass.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.listeners.playertimers.WarmupTimer;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PvPClassListener extends Module<PvPClassManager> {
    public PvPClassListener(PvPClassManager manager) {
        super(manager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            this.getManager().checkArmor(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PvPClass pvpClass = this.getManager().getActiveClass(player);
        if (pvpClass != null) {
            pvpClass.unEquip(player);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PvPClass pvpClass = this.getManager().getActiveClass(player);
        if (pvpClass != null) {
            Tasks.execute(this.getManager(), () -> pvpClass.addEffects(player));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PvPClass pvpClass = this.getManager().getActiveClass(player);
        if (pvpClass != null) {
            pvpClass.unEquip(player);
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getTimer() instanceof WarmupTimer)) {
            return;
        }
        Player player = Bukkit.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }
        WarmupTimer timer = (WarmupTimer) event.getTimer();
        PvPClass pvpClass = timer.getWarmups().get(player.getUniqueId());
        if (pvpClass != null) {
            Tasks.execute(this.getManager(), () -> timer.getWarmups().remove(player.getUniqueId()).equip(player));
        }
    }
}
