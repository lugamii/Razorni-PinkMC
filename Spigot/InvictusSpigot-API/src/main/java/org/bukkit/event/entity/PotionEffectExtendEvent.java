package org.bukkit.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class PotionEffectExtendEvent extends PotionEffectAddEvent {
    private final PotionEffect oldEffect;

    public PotionEffectExtendEvent(LivingEntity entity, PotionEffect effect, PotionEffect oldEffect, PotionEffectAddEvent.EffectCause cause) {
        super(entity, effect, cause);
        this.oldEffect = oldEffect;
    }

    public PotionEffect getOldEffect() {
        return this.oldEffect;
    }
}
