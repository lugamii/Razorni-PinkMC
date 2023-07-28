package dev.razorni.crates.crate;

import dev.razorni.crates.Crates;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


@Getter
public class CrateManager {

    private static final String CRATE_PATH
            = JavaPlugin.getPlugin(Crates.class).getDataFolder() + "/crates/";

    private final Map<UUID, Crate> crateMap = new HashMap<>();
    private final Map<UUID, Crate> crateEditingMap = new HashMap<>();

    private final Crates plugin;

    public CrateManager(Crates plugin) {
        this.plugin = plugin;

        loadCrates();
    }

    public void loadCrates() {
        File path = new File(CRATE_PATH);
        if (!path.exists())
            if (path.mkdir())
                Bukkit.getLogger().info("Default crate path didn't exist, so we created it for you!");

        File[] files = path.listFiles();

        if (files == null)
            return;

        for (File file : files)
            loadCrate(file);
    }


    public void loadCrate(Crate crate) {
        crate.deleteHologram();
        loadCrate(new File(CRATE_PATH, crate.getName() + ".json"));
    }

    public Crate loadCrate(File file) {
        AtomicReference<Crate> crate = new AtomicReference<>();

        crate.set(plugin.getConfigurationService().loadConfiguration(Crate.class, file));
        crateMap.put(crate.get().getUuid(), crate.get());

        Location location = crate.get().getLocation();
        if (location != null)
            location.getBlock().setMetadata("crate",
                    new FixedMetadataValue(plugin, crate.get().getName()));

        crate.get().updateHologram();

        return crate.get();
    }

    public void saveCrates(boolean async) {
        crateMap.values().forEach(crate -> saveCrate(crate, async));
    }

    public void saveCrate(Crate crate, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveCrate(crate, false));
            return;
        }

        try {
            plugin.getConfigurationService().saveConfiguration(crate,
                    new File(CRATE_PATH, crate.getName() + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Crate createCrate(String name) {
        Crate crate = new Crate(name);
        crateMap.put(crate.getUuid(), crate);
        saveCrate(crate, true);
        return crate;
    }

    public void deleteCrate(Crate crate) {
        crate.deleteHologram();
        File file = new File(CRATE_PATH, crate.getName() + ".json");
        if (file.delete()) {
            crateMap.remove(crate.getUuid());
            plugin.getLogger().info("Crate " + crate.getName() + " has been deleted.");
        }
    }

    public Crate getCrate(UUID uuid) {
        return crateMap.get(uuid);
    }

    public Crate getCrate(String name) {
        return crateMap.values().stream()
                .filter(crate -> crate.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Crate getCrate(ItemStack itemStack) {
        for (UUID uuid : crateMap.keySet()) {
            Crate crate = getCrate(uuid);
            if (crate.getKey().isSimilar(itemStack))
                return crate;
        }

        return null;
    }


    public Crate getCrate(Location location) {
        for (UUID uuid : crateMap.keySet()) {
            Crate crate = getCrate(uuid);
            if (crate.getLocation() != null && crate.getLocation().equals(location))
                return crate;
        }

        return null;
    }

    public Crate isBuiltCrate(ItemStack itemStack) {
        for (Crate crate : crateMap.values())
            if (crate.buildCrate().isSimilar(itemStack))
                return crate;

        return null;
    }

    public boolean crateExists(String name) {
        return getCrate(name) != null;
    }

    public Map<UUID, Crate> getCrateMap() {
        return crateMap;
    }

}
