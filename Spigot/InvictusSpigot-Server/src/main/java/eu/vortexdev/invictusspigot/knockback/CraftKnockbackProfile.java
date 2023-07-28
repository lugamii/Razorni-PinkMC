package eu.vortexdev.invictusspigot.knockback;

import eu.vortexdev.api.knockback.KnockbackValue;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("KnockbackProfile")
public class CraftKnockbackProfile implements eu.vortexdev.api.knockback.KnockbackProfile, ConfigurationSerializable {
    private final String name;
    private final List<KnockbackValue> settings = Arrays.asList(
            new CraftKnockbackValue("hitDelay", 19),
            new CraftKnockbackValue("slowdown", 0.6D),
            new CraftKnockbackValue("cancelSprint", true),

            new CraftKnockbackValue("horizontal", 0.43D),
            new CraftKnockbackValue("horizontalOnGround", 1.1D),
            new CraftKnockbackValue("horizontalSprinting", 1.2D),
            new CraftKnockbackValue("inheritHorizontal", true),
            new CraftKnockbackValue("frictionHorizontal", 1.0D),

            new CraftKnockbackValue("17kb", false),
            new CraftKnockbackValue("17kbHorizontal", 0.25),

            new CraftKnockbackValue("vertical", 0.33D),
            new CraftKnockbackValue("verticalOnGround", 1.0D),
            new CraftKnockbackValue("verticalSprinting", 1.0D),

            new CraftKnockbackValue("comboMode", false),
            new CraftKnockbackValue("comboHeight", 2.3D),
            new CraftKnockbackValue("comboTicks", 10),
            new CraftKnockbackValue("comboVelocity", -0.05D),

            new CraftKnockbackValue("rodHorizontal", 1.1),
            new CraftKnockbackValue("rodVertical", 1.0D),

            new CraftKnockbackValue("bowHozirontal", 1.1),
            new CraftKnockbackValue("bowPunchMultiplier", 1.0),
            new CraftKnockbackValue("bowVertical", 1.0D)
    );

    public CraftKnockbackProfile(String name) {
        this.name = name;
    }

    public CraftKnockbackProfile(Map<String, Object> args) {
        name = (String) args.get("name");
        for (String key : args.keySet()) {
            KnockbackValue setting = getSetting(key);
            if (setting != null)
                setting.setValue(args.get(key));
        }
    }

    public String getName() {
        return name;
    }

    public List<KnockbackValue> getSettings() {
        return settings;
    }

    public KnockbackValue getSetting(String key) {
        for (KnockbackValue setting : settings)
            if (setting.getKey().equalsIgnoreCase(key))
                return setting;
        return null;
    }

    public <T> T getSettingValue(String key) {
        return (T) Objects.requireNonNull(getSetting(key)).getValue();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        for (KnockbackValue setting : settings) {
            result.put(setting.getKey(), setting.getValue());
        }
        return result;
    }

}
