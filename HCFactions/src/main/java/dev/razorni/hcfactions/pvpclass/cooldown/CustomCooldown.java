package dev.razorni.hcfactions.pvpclass.cooldown;

import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import lombok.Getter;

@Getter
public class CustomCooldown extends Cooldown {
    private final String displayName;

    public CustomCooldown(PvPClass pvpClass, String name) {
        super(pvpClass.getManager());
        this.displayName = (name.isEmpty() ? null : name);
        pvpClass.getCustomCooldowns().add(this);
    }

}