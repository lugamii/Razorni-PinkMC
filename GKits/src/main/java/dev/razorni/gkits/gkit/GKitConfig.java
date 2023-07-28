package dev.razorni.gkits.gkit;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import cc.invictusgames.ilib.configuration.StaticConfiguration;
import cc.invictusgames.ilib.configuration.defaults.MongoConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class GKitConfig implements StaticConfiguration {

    private MongoConfig mongoConfig = new MongoConfig();
    private String mongoDatabase = "staging-gkit";
    private int inventorySize = 54;

    private Map<String, Integer> enchantPrices = new HashMap<String, Integer>() {{
        put("fireresistance", 30);
        put("hellforged", 35);
        put("implants", 40);
        put("recover", 30);
        put("speed", 25);
    }};

    public Integer getEnchantPrice(CustomEnchant enchant) {
        String name = enchant.getName().replace(" ", "");
        return enchantPrices.getOrDefault(name.toLowerCase(), -1);
    }

    public void saveConfig() {
        try {
            GKits.get().getConfigurationService().saveConfiguration(this,
                    new File(GKits.get().getDataFolder(), "config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
