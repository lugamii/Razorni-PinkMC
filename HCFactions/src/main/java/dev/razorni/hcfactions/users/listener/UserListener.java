package dev.razorni.hcfactions.users.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.timers.listeners.playertimers.InvincibilityTimer;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class UserListener extends Module<UserManager> {
    public UserListener(UserManager manager) {
        super(manager);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (!this.getManager().isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Server has not fully loaded!");
            return;
        }
        if (!this.getManager().getUsers().containsKey(uuid)) {
            try {
                User user = new User(this.getManager(), uuid);
                user.save();
                if (!this.getInstance().getTimerManager().getSotwTimer().isActive()) {
                    InvincibilityTimer lllllllllllllllllIIlIlIIlIIIIIll = this.getInstance().getTimerManager().getInvincibilityTimer();
                    lllllllllllllllllIIlIlIIlIIIIIll.getTimerCache().put(uuid, System.currentTimeMillis() + 1000L * lllllllllllllllllIIlIlIIlIIIIIll.getSeconds());
                }
                this.getManager().getUsers().put(uuid, user);
            } catch (Exception e) {
                e.printStackTrace();
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("Error loading user data!");
            }
        }
        if (!this.getManager().getUsers().containsKey(uuid)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Error loading user data!");
        }
    }



}
