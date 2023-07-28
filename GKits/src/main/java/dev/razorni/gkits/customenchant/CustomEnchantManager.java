package dev.razorni.gkits.customenchant;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.impl.*;
import dev.razorni.gkits.customenchant.listener.ArmorEquipListener;
import dev.razorni.gkits.customenchant.listener.CustomEnchantListener;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class CustomEnchantManager {

    private final GKits plugin;
    private final List<CustomEnchant> customEnchantList = new ArrayList<>();

    public CustomEnchantManager(GKits plugin) {
        this.plugin = plugin;

        registerEnchant(
                new SpeedEnchant(),
                new FireResistanceEnchant(),
                new HellForgedEnchant(),
                new RecoverEnchant(),
                new ImplantsEnchant()
        );

        Stream.of(new ArmorEquipListener(plugin),
                        new CustomEnchantListener(plugin))
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
    }

    public void registerEnchant(CustomEnchant... customEnchants) {
        customEnchantList.addAll(Arrays.asList(customEnchants));
    }

    public CustomEnchant getEnchantByDisplayName(String displayName) {
        for (CustomEnchant customEnchant : customEnchantList)
            if (customEnchant.getDisplayName().equals(displayName))
                return customEnchant;

        return null;
    }

    public CustomEnchant getEnchantByName(String name) {
        for (CustomEnchant customEnchant : customEnchantList)
            if (customEnchant.getName().replace(" ", "").equalsIgnoreCase(name))
                return customEnchant;

        return null;
    }
}
