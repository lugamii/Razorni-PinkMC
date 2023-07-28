package dev.razorni.hcfactions.pvpclass.cooldown;

import dev.razorni.hcfactions.pvpclass.PvPClass;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;

@Getter
public class ClassBuff extends CustomCooldown {
    private final PotionEffect effect;
    private final int cooldown;

    public ClassBuff(PvPClass pvpClass, String name, PotionEffect effect, int cooldown) {
        super(pvpClass, name);
        this.effect = effect;
        this.cooldown = cooldown;
    }

}