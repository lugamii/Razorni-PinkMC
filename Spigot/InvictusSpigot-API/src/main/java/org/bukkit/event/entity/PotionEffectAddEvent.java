package org.bukkit.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

public class PotionEffectAddEvent extends PotionEffectEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final EffectCause effectCause;
    private boolean cancelled;

    public PotionEffectAddEvent(LivingEntity entity, PotionEffect effect, EffectCause effectCause) {
        super(entity, effect);
        this.effectCause = effectCause;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EffectCause getCause() {
        return this.effectCause;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static enum EffectCause {
        POTION_SPLASH,
        BEACON,
        WITHER_SKELETON,
        WITHER_SKULL,
        PLUGIN,
        UNKNOWN;
    }
}
