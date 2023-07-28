package dev.razorni.hcfactions.extras.spawners;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.extras.spawners.listener.SpawnerListener;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SpawnerManager extends Manager {
    private final Map<EntityType, Spawner> spawners;

    public SpawnerManager(HCF plugin) {
        super(plugin);
        this.spawners = new HashMap<>();
        this.load();
        new SpawnerListener(this);
    }

    public Spawner getByName(String name) {
        if (name.equalsIgnoreCase("skele")) {
            return this.spawners.get(EntityType.SKELETON);
        }
        try {
            return this.spawners.get(EntityType.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public Spawner getByItem(ItemStack stack) {
        for (Spawner spawner : this.spawners.values()) {
            if (!spawner.getItemStack().isSimilar(stack)) {
                continue;
            }
            return spawner;
        }
        return null;
    }

    private void load() {
        for (String s : this.getConfig().getStringList("SPAWNERS_CONFIG.TYPES")) {
            String[] types = s.split(", ");
            this.spawners.put(EntityType.valueOf(types[0]), new Spawner(this, EntityType.valueOf(types[0]), types[1]));
        }
    }
}