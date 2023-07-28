package dev.razorni.hcfactions.utils.configs;

import dev.razorni.hcfactions.HCF;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigJson {

    private final File file;
    private final HCF instance;
    private final String name;
    private final Map<String, Object> values;

    public ConfigJson(HCF plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        this.values = new ConcurrentHashMap<>();
        this.instance = plugin;
        this.name = name;
        this.load();
    }

    public void save() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8);
            this.instance.getGson().toJson(this.values, outputStreamWriter);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void load() {
        try {
            if (!this.file.exists()) {
                this.instance.saveResource(this.name, false);
            }
            FileReader fileReader = new FileReader(this.file);
            Map<String, Object> strings = (Map<String, Object>) this.instance.getGson().fromJson(fileReader, (Class) Map.class);
            this.values.putAll(strings);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
