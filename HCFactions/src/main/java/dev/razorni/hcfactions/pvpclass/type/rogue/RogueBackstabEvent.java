package dev.razorni.hcfactions.pvpclass.type.rogue;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
@Setter
public class RogueBackstabEvent extends EntityDamageEvent {
    private Player backstabbedBy;

    public RogueBackstabEvent(Entity entity, Player damager, DamageCause cause, double damage) {
        super(entity, cause, damage);
        this.backstabbedBy = damager;
    }
}
