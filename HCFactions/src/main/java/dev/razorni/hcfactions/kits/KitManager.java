package dev.razorni.hcfactions.kits;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public class KitManager extends Manager {
    private final Map<String, Kit> kits;

    public KitManager(HCF plugin) {
        super(plugin);
        this.kits = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.load();
    }

    private void load() {
        new Kit(this, "deathban").save();
        new Kit(this, "ktk").save();
        Map<String, Object> values = this.getKitsData().getValues();
        for (String s : values.keySet()) {
            Map<String, Object> map = (Map<String, Object>) values.get(s);
            Kit kit = new Kit(this, map);
            this.kits.put(kit.getName(), kit);
        }
    }

    public Kit getKit(String kit) {
        return this.kits.get(kit);
    }

    @Override
    public void disable() {
        for (Kit kit : this.kits.values()) {
            kit.save();
        }
    }
}
