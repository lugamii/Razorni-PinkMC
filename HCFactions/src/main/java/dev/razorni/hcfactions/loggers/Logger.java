package dev.razorni.hcfactions.loggers;

import dev.razorni.hcfactions.extras.framework.Module;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class Logger extends Module<LoggerManager> {

    private final BukkitTask removeTask;
    private final Villager villager;
    private final ItemStack[] armorContents;
    private final float exp;
    private final ItemStack[] contents;
    private final Player player;


    public Logger(LoggerManager loggermanager, Player player) {
        super(loggermanager);
        this.player = player;
        this.villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.exp = player.getExp();
        this.removeTask = Bukkit.getScheduler().runTaskLater(this.getInstance(), () -> this.getManager().removeLogger(player), this.getConfig().getInt("LOGGERS.DESPAWN") * 20L);
        this.checkVillager();
    }

    private void checkVillager() {
        this.villager.setCustomName(this.getConfig().getString("LOGGERS.COLOR") + this.player.getName());
        this.villager.setCustomNameVisible(true);
        this.villager.setMaxHealth(40.0);
        this.villager.setHealth(this.villager.getMaxHealth());
        this.villager.setAdult();
        this.villager.setBreed(false);
        this.villager.setProfession(Villager.Profession.FARMER);
        this.villager.setFallDistance(this.player.getFallDistance());
        this.villager.setVelocity(this.player.getVelocity());
        this.villager.setRemoveWhenFarAway(true);
        for (PotionEffect effect : this.player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.FIRE_RESISTANCE) {
                this.villager.addPotionEffect(effect);
            }
        }
        this.villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100, false));
        this.villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100, false));
    }
}
