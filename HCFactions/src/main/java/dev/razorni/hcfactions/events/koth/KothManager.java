package dev.razorni.hcfactions.events.koth;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.events.EventType;
import dev.razorni.hcfactions.events.koth.listener.KothListener;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class KothManager extends Manager {
    private final Table<String, Long, Koth> captureZones;
    private final Map<String, Koth> koths;

    public KothManager(HCF plugin) {
        super(plugin);
        this.koths = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.captureZones = HashBasedTable.create();
        this.load();
        new KothListener(this);
    }

    public Table<String, Long, Koth> getCaptureZones() {
        return this.captureZones;
    }

    public Koth getZone(Location location) {
        Koth koth = this.captureZones.get(location.getWorld().getName(), this.toLong(location.getBlockX(), location.getBlockZ()));
        if (koth != null) {
            int y = location.getBlockY();
            if (y > koth.getCaptureZone().getMaximumY() || y < koth.getCaptureZone().getMinimumY()) {
                return null;
            }
        }
        return koth;
    }

    public long toLong(int i1, int i2) {
        return ((long) i1 << 32) + (long) i2 - Integer.MIN_VALUE;
    }

    private void load() {
        for (Object value : this.getEventsData().getValues().values()) {
            Map<String, Object> map = (Map<String, Object>) value;
            if (EventType.valueOf((String) map.get("type")) != EventType.KOTH) {
                continue;
            }
            Koth koth = new Koth(this, map);
            koth.save();
        }
        Tasks.executeScheduled(this, 20, () -> {
            for (Koth koth : koths.values()) {
                if (koth.isActive()) {
                    koth.tick();
                }
            }
        });
    }

    @Override
    public void disable() {
        for (Koth koth : this.koths.values()) {
            koth.save();
        }
    }

    public List<Koth> getActiveKoths() {
        List<Koth> koths = new ArrayList<>();
        for (Koth koth : this.getKoths().values()) {
            if (koth.isActive()) {
                koths.add(koth);
            }
        }
        return koths;
    }

    public Koth getKoth(String name) {
        return this.koths.get(name);
    }
}
