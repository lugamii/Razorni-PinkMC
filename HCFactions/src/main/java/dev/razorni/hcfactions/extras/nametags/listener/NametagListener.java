package dev.razorni.hcfactions.extras.nametags.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.nametags.Nametag;
import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.utils.versions.type.Version1_8_R3;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class NametagListener extends Module<NametagManager> {
    public NametagListener(NametagManager manager) {
        super(manager);
        this.load();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getNametags().remove(player.getUniqueId());
    }

    private void load() {
        if (this.getInstance().getVersionManager().getVersion() instanceof Version1_8_R3) {
            this.manager.registerListener(new Listener() {
                @EventHandler
                public void onRemove(PotionEffectRemoveEvent event) {
                    this.check(event);
                }

                private void check(PotionEffectEvent event) {
                    if (!(event.getEntity() instanceof Player)) {
                        return;
                    }
                    if (!event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
                        return;
                    }
                    NametagListener.this.getManager().update();
                }

                @EventHandler
                public void onExpire(PotionEffectExpireEvent event) {
                    this.check(event);
                }

                @EventHandler
                public void onAdd(PotionEffectAddEvent event) {
                    this.check(event);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getManager().getNametags().put(player.getUniqueId(), new Nametag(this.getManager(), player));
        this.getManager().update();
    }
}
