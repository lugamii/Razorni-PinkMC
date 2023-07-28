package dev.razorni.hcfactions.extras.framework.extra;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.configs.ConfigJson;
import dev.razorni.hcfactions.utils.configs.ConfigYML;

import java.io.File;

public class Configs {
    private static ConfigYML TEAM_CONFIG;
    private static ConfigJson EVENTS_DATA;
    private static ConfigYML ITEMS_CONFIG;
    private static ConfigYML TABLIST_CONFIG;
    private static ConfigYML LANGUAGE_CONFIG;
    private static ConfigYML ABILITIES_CONFIG;
    private static ConfigYML LUNAR_CONFIG;
    private static ConfigJson KITS_DATA;
    private static ConfigYML SCOREBOARD_CONFIG;
    private static ConfigYML RECLAIMS_CONFIG;
    private static ConfigYML CONFIG;
    private static ConfigYML LIMITERS_CONFIG;
    private static ConfigYML CLASSES_CONFIG;
    private static ConfigYML SCHEDULES_CONFIG;

    public ConfigYML getConfig() {
        return Configs.CONFIG;
    }

    public ConfigYML getLimitersConfig() {
        return Configs.LIMITERS_CONFIG;
    }

    public ConfigYML getTablistConfig() {
        return Configs.TABLIST_CONFIG;
    }

    public ConfigYML getAbilitiesConfig() {
        return Configs.ABILITIES_CONFIG;
    }

    public ConfigJson getKitsData() {
        return Configs.KITS_DATA;
    }

    public ConfigYML getLunarConfig() {
        return Configs.LUNAR_CONFIG;
    }

    public ConfigYML getClassesConfig() {
        return Configs.CLASSES_CONFIG;
    }

    public ConfigYML getLanguageConfig() {
        return Configs.LANGUAGE_CONFIG;
    }

    public ConfigYML getItemsConfig() {
        return Configs.ITEMS_CONFIG;
    }

    public void load(HCF plugin) {
        Configs.CONFIG = new ConfigYML(plugin, "config");
        Configs.SCOREBOARD_CONFIG = new ConfigYML(plugin, "scoreboard");
        Configs.LANGUAGE_CONFIG = new ConfigYML(plugin, "language");
        Configs.TEAM_CONFIG = new ConfigYML(plugin, "teams");
        Configs.LUNAR_CONFIG = new ConfigYML(plugin, "lunar");
        Configs.TABLIST_CONFIG = new ConfigYML(plugin, "tablist");
        Configs.CLASSES_CONFIG = new ConfigYML(plugin, "classes");
        Configs.RECLAIMS_CONFIG = new ConfigYML(plugin, "reclaims");
        Configs.LIMITERS_CONFIG = new ConfigYML(plugin, "limiters");
        Configs.ABILITIES_CONFIG = new ConfigYML(plugin, "abilities");
        Configs.SCHEDULES_CONFIG = new ConfigYML(plugin, "schedules");
        Configs.EVENTS_DATA = new ConfigJson(plugin, String.valueOf("data" + File.separator + "events.json"));
        Configs.KITS_DATA = new ConfigJson(plugin, String.valueOf("data" + File.separator + "kits.json"));
        Configs.ITEMS_CONFIG = (Utils.getNMSVer().equalsIgnoreCase("1_16_R3") ? new ConfigYML(plugin, String.valueOf(new StringBuilder().append("items").append(File.separator).append("items1.16"))) : new ConfigYML(plugin, String.valueOf(new StringBuilder().append("items").append(File.separator).append("items"))));
        new ItemUtils(this);
        new Config(this);
    }

    public ConfigYML getSchedulesConfig() {
        return Configs.SCHEDULES_CONFIG;
    }

    public ConfigYML getScoreboardConfig() {
        return Configs.SCOREBOARD_CONFIG;
    }

    public ConfigYML getTeamConfig() {
        return Configs.TEAM_CONFIG;
    }

    public ConfigJson getEventsData() {
        return Configs.EVENTS_DATA;
    }

    public ConfigYML getReclaimsConfig() {
        return Configs.RECLAIMS_CONFIG;
    }
}
