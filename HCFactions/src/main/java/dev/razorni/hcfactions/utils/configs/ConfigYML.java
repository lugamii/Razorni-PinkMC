package dev.razorni.hcfactions.utils.configs;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigYML extends YamlConfiguration {
    private final Map<String, Object> map;
    private final File file;

    public ConfigYML(HCF plugin, String fileName) {
        this.file = new File(plugin.getDataFolder(), fileName + ".yml");
        this.map = new HashMap<>();
        plugin.getConfigs().add(this);
        if (!this.file.exists()) {
            plugin.saveResource(fileName + ".yml", false);
        }
        this.reload();
    }

    public long getLong(String input) {
        return (Long) this.map.computeIfAbsent(input, super::getLong);
    }

    public List<String> getStringList(String input) {
        return new ArrayList<>(CC.t(super.getStringList(input)));
    }

    public double getDouble(String input) {
        return (Double) this.map.computeIfAbsent(input, super::getDouble);
    }

    public boolean getBoolean(String input) {
        return (Boolean) this.map.computeIfAbsent(input, super::getBoolean);
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadCache() {
        this.map.clear();
    }

    public int getInt(String input) {
        return (Integer) this.map.computeIfAbsent(input, super::getInt);
    }

    public String getString(String input) {
        return (String) this.map.computeIfAbsent(input, s -> CC.t(super.getString(input)));
    }

    public void reload() {
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getConfigurationSection(String input) {
        return (ConfigurationSection) this.map.computeIfAbsent(input, super::getConfigurationSection);
    }
}