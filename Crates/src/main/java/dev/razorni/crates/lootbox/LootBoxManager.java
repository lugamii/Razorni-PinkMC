package dev.razorni.crates.lootbox;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.listener.LootBoxListener;
import dev.razorni.crates.lootbox.profile.listener.LootBoxProfileListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class LootBoxManager {

    private static final String LOOTBOX_PATH
            = JavaPlugin.getPlugin(Crates.class).getDataFolder() + "/lootboxes/";

    private final Map<UUID, LootBox> lootBoxMap = new HashMap<>();

    @Getter
    private final Map<UUID, LootBox> lootBoxEditingMap = new HashMap<>();

    @Getter
    private final Map<UUID, LootBox> lootBoxFinalEditingMap = new HashMap<>();

    private final Crates plugin;

    public LootBoxManager(Crates plugin) {
        this.plugin = plugin;

        Stream.of(new LootBoxListener(plugin), new LootBoxProfileListener())
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));

        loadLootBoxes();
    }

    public void loadLootBoxes() {
        File path = new File(LOOTBOX_PATH);
        if (!path.exists())
            if (path.mkdir())
                Bukkit.getLogger().info("Default lootbox path didn't exist, so we created it for you!");

        File[] files = path.listFiles();

        if (files == null)
            return;


        for (File file : files)
            loadLootBox(file);
    }


    public void loadLootBox(LootBox lootBox) {
        loadLootBox(new File(LOOTBOX_PATH, lootBox.getName() + ".json"));
    }

    public void loadLootBox(File file) {
        AtomicReference<LootBox> lootBox = new AtomicReference<>();

        CompletableFuture.runAsync(() -> {
            lootBox.set(plugin.getConfigurationService().loadConfiguration(LootBox.class, file));
            lootBoxMap.put(lootBox.get().getUuid(), lootBox.get());
        });
    }

    public void saveLootBoxes(boolean async) {
        lootBoxMap.values().forEach(lootbox -> saveLootBox(lootbox, async));
    }

    public void saveLootBox(LootBox lootbox, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveLootBox(lootbox, false));
            return;
        }

        try {
            plugin.getConfigurationService().saveConfiguration(lootbox,
                    new File(LOOTBOX_PATH, lootbox.getName() + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LootBox createLootBox(String name) {
        LootBox lootBox = new LootBox(name);
        lootBoxMap.put(lootBox.getUuid(), lootBox);
        saveLootBox(lootBox, true);
        return lootBox;
    }

    public void deleteLootBox(LootBox lootBox) {
        File file = new File(LOOTBOX_PATH, lootBox.getName() + ".json");
        if (file.delete()) {
            lootBoxMap.remove(lootBox.getUuid());
            plugin.getLogger().info("LootBox " + lootBox.getName() + " has been deleted.");
        }
    }

    public LootBox getLootBox(UUID uuid) {
        return lootBoxMap.get(uuid);
    }


    public LootBox getLootBox(ItemStack itemStack) {
        for (UUID uuid : lootBoxMap.keySet()) {
            LootBox lootBox = getLootBox(uuid);
            if (lootBox.getLootBox(itemStack.getAmount()).isSimilar(itemStack))
                return lootBox;
        }

        return null;
    }


    public LootBox getLootBox(String name) {
        return lootBoxMap.values().stream()
                .filter(lootBox -> lootBox.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public String transformName(String string) {
        return ChatColor.stripColor(string).replaceAll(" lootbox.", "");
    }

    public boolean lootBoxExists(String name) {
        return getLootBox(name) != null;
    }

    public Map<UUID, LootBox> getLootBoxMap() {
        return lootBoxMap;
    }

}
