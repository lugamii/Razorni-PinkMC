package dev.razorni.core.extras.tips.file;

import dev.razorni.core.Core;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class TipFile extends YamlConfiguration {

    public static TipFile config;
    private final Plugin plugin;
    private final java.io.File configFile;

    public TipFile() {
        this.plugin = main();
        this.configFile = new java.io.File(this.plugin.getDataFolder(), "tips.yml");
        saveDefault();
        reload();
    }

    public static TipFile getConfig() {
        if (config == null) {
            config = new TipFile();
        }
        return config;
    }

    private Plugin main() {
        return Core.getInstance();
    }

    public void reload() {
        try {
            super.load(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        this.plugin.saveResource("tips.yml", false);
    }
}
