package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NinjaAbility extends Ability {
    private int seconds;
    private int hitsValid;

    public NinjaAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Ninja Ability");
        this.seconds = this.getAbilitiesConfig().getInt("NINJA_ABILITY.SECONDS");
        this.hitsValid = this.getAbilitiesConfig().getInt("NINJA_ABILITY.HITS_VALID");
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        Player manager = ((FocusModeAbility) this.getManager().getAbility("FocusMode")).getDamager(player, this.hitsValid);
        if (manager == null) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.NINJA_ABILITY.NO_LAST_HIT"));
            return;
        }
        this.takeItem(player);
        this.applyCooldown(player);
        new BukkitRunnable() {
            private int i = 0;

            public void run() {
                if (this.i == NinjaAbility.this.seconds) {
                    player.teleport(manager);
                    this.cancel();
                    for (String s : NinjaAbility.this.getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTED_SUCCESSFULLY")) {
                        player.sendMessage(s.replaceAll("%player%", manager.getName()));
                    }
                    return;
                }
                for (String s : NinjaAbility.this.getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTING")) {
                    player.sendMessage(s.replaceAll("%player%", manager.getName()).replaceAll("%seconds%", String.valueOf(NinjaAbility.this.seconds - this.i)));
                }
                for (String s : NinjaAbility.this.getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTING_ATTACKER")) {
                    manager.sendMessage(s.replaceAll("%player%", player.getName()).replaceAll("%seconds%", String.valueOf(NinjaAbility.this.seconds - this.i)));
                }
                ++this.i;
            }
        }.runTaskTimer(this.getInstance(), 0L, 20L);
    }
}
