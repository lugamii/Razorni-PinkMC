package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Iterator;

public class StackListener extends Module<ListenerManager> {
    public StackListener(ListenerManager manager) {
        super(manager);
        Tasks.executeScheduledAsync(this.getManager(), 6000, this::clean);
    }

    private void setAmount(LivingEntity entity, int amount) {
        entity.setCustomName(this.getConfig().getString("MOB_STACKING.COLOR") + amount + "x");
        entity.setCustomNameVisible(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!this.getConfig().getBoolean("MOB_STACKING.ENABLED")) {
            return;
        }
        LivingEntity entity = event.getEntity();
        int amount = this.getAmount(entity);
        if (amount == -1) {
            return;
        }
        if (amount == 1) {
            return;
        }
        LivingEntity ent = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
        if (ent instanceof Ageable) {
            Ageable ageable = (Ageable) ent;
            if (!ageable.isAdult()) {
                ageable.setAdult();
            }
        }
        this.setAmount(ent, amount - 1);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!this.getConfig().getBoolean("MOB_STACKING.ENABLED")) {
            return;
        }
        LivingEntity entity = event.getEntity();
        boolean b = true;
        int radius = this.getConfig().getInt("MOB_STACKING.RADIUS");
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }
        if (entity.getType() == EntityType.ENDERMAN) {
            return;
        }
        if (entity.getType() == EntityType.VILLAGER) {
            return;
        }
        for (Entity e : entity.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof LivingEntity)) {
                continue;
            }
            if (e.getType() != entity.getType()) {
                continue;
            }
            LivingEntity le = (LivingEntity) e;
            int amount = this.getAmount(le);
            if (amount == -1) {
                continue;
            }
            if (amount >= this.getConfig().getInt("MOB_STACKING.MAX_STACK")) {
                continue;
            }
            event.setCancelled(true);
            this.setAmount(le, amount + 1);
            b = false;
        }
        if (b) {
            this.setAmount(entity, 1);
        }
    }

    private void clean() {
        for (World world : Bukkit.getWorlds()) {
            Iterator<LivingEntity> entities = world.getLivingEntities().iterator();
            while (entities.hasNext()) {
                LivingEntity entity = entities.next();
                if (this.getAmount(entity) == -1) {
                    continue;
                }
                entities.remove();
                entity.remove();
            }
        }
    }

    private int getAmount(LivingEntity entity) {
        String name = entity.getCustomName();
        if (name == null) {
            return -1;
        }
        try {
            return Integer.parseInt(ChatColor.stripColor(name).replaceAll("x", ""));
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
