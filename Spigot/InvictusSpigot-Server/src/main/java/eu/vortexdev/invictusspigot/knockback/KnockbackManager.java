package eu.vortexdev.invictusspigot.knockback;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import eu.vortexdev.api.knockback.KnockbackProfile;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class KnockbackManager {
    private static final String HEADER = " \nThis is the main configuration for knockback\nATTENTION: Do not rename \"Default\" knockback profile\n \n";
    public static KnockbackProfile DEFAULT_KNOCKBACK = new CraftKnockbackProfile("Default");

    private FileConfiguration config;
    private final File file = new File("knockback.yml");
    private final List<KnockbackProfile> profiles = Lists.newArrayList();

    public List<KnockbackProfile> getProfiles() {
        return ImmutableList.copyOf(profiles);
    }

    public KnockbackProfile getProfile(String name) {
        return profiles.stream().filter(profile -> profile.getName().equals(name)).findFirst().orElse(null);
    }

    public void saveProfiles() {
        DEFAULT_KNOCKBACK = getProfile("Default");
        config.set("profiles", profiles);
        JavaUtil.saveFile(config, file);
    }

    public void reloadProfiles() {
        profiles.clear();
        JavaUtil.createIfNotExists(file);
        config = YamlConfiguration.loadConfiguration(file);
        config.options().header(HEADER);
        profiles.addAll(JavaUtil.createList(config.get("profiles"), CraftKnockbackProfile.class));
        KnockbackProfile defaultProfile = getProfile("Default");
        if (defaultProfile != null) {
            DEFAULT_KNOCKBACK = defaultProfile;
        } else {
            addProfile(DEFAULT_KNOCKBACK);
        }
    }

    public void addProfile(KnockbackProfile profile) {
        profiles.add(profile);
        saveProfiles();
    }

    public void removeProfile(KnockbackProfile profile) {
        profiles.remove(profile);
        saveProfiles();
    }
}
