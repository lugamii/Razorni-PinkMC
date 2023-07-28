package dev.razorni.hcfactions.timers.listeners.playertimers;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.timers.type.PlayerTimer;

public class AbilityTimer extends PlayerTimer {
    private final Ability ability;

    public AbilityTimer(TimerManager manager, Ability ability, String name) {
        super(manager, false, ability.getName().replaceAll(" ", ""), name, manager.getAbilitiesConfig().getInt(ability.getNameConfig() + ".COOLDOWN"));
        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }
}
