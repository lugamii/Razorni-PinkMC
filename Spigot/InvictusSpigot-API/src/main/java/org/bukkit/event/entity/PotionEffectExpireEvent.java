package org.bukkit.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class PotionEffectExpireEvent extends PotionEffectRemoveEvent {
    private int duration = 0;

    public PotionEffectExpireEvent(LivingEntity entity, PotionEffect effect) {
        super(entity, effect);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }

    public boolean isCancelled() {
        return this.duration > 0;
    }

    public void setCancelled(boolean cancel) {
        this.duration = cancel ? 2147483647 : 0;
    }
}
