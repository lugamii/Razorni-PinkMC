package dev.razorni.gkits;

import dev.razorni.gkits.customenchant.CustomEnchant;
import dev.razorni.gkits.customenchant.CustomEnchantManager;
import dev.razorni.gkits.customenchant.command.CustomEnchantCommands;
import dev.razorni.gkits.customenchant.command.parameter.CustomEnchantParameter;
import dev.razorni.gkits.gkit.GKit;
import dev.razorni.gkits.gkit.GKitConfig;
import dev.razorni.gkits.gkit.GKitManager;
import dev.razorni.gkits.gkit.command.GKitCommands;
import dev.razorni.gkits.gkit.command.parameter.GKitParameter;
import dev.razorni.gkits.gkit.menu.GKitMenuConfig;
import dev.razorni.gkits.hook.GKitPluginHook;
import dev.razorni.gkits.mongo.MongoManager;
import dev.razorni.gkits.profile.ProfileManager;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.configuration.ConfigurationService;
import cc.invictusgames.ilib.configuration.JsonConfigurationService;
import cc.invictusgames.ilib.utils.logging.BukkitLogFactory;
import cc.invictusgames.ilib.utils.logging.LogFactory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class GKits extends JavaPlugin {

    private ExecutorService executorService;

    private ConfigurationService configurationService;
    private GKitConfig gKitConfig;
    private GKitMenuConfig gKitMenuConfig;
    private MongoManager mongoManager;
    private GKitManager gKitManager;
    private ProfileManager profileManager;
    private CustomEnchantManager customEnchantManager;
    private LogFactory logFactory;

    @Setter
    private GKitPluginHook pluginHook = GKitPluginHook.DEFAULT_HOOK;

    @Override
    public void onEnable() {
        executorService = Executors.newSingleThreadExecutor();
        configurationService = new JsonConfigurationService();
        logFactory = new BukkitLogFactory(this);

        if (!getDataFolder().exists())
            if (getDataFolder().mkdir())
                getLogger().info("Plugin directory didn't exist, so we created it for you!");

        loadGKitConfig();
        loadGKitMenuConfig();

        if (gKitConfig.getMongoDatabase()
                .equalsIgnoreCase("default")) {
            Bukkit.getLogger().info("Mongo Database is set to default," +
                    " please change it. Shutting down plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        mongoManager = new MongoManager(this);
        if (mongoManager.connect())
            System.out.println("[GKits] Succesfully connected to MongoDB");

        gKitManager = new GKitManager(this);
        profileManager = new ProfileManager(this);
        customEnchantManager = new CustomEnchantManager(this);

        CommandService.registerParameter(CustomEnchant.class, new CustomEnchantParameter(this));
        CommandService.registerParameter(GKit.class, new GKitParameter(this));
        CommandService.register(this,
                new GKitCommands(this, gKitManager),
                new CustomEnchantCommands(this));
    }

    @Override
    public void onDisable() {
        gKitManager.saveGKits(false);
        profileManager.getProfileMap().values().forEach(profile -> profile.save(false));
    }

    public void loadGKitMenuConfig() {
        gKitMenuConfig = configurationService.loadConfiguration(GKitMenuConfig.class,
                new File(getDataFolder(), "menu.json"));
    }

    public void loadGKitConfig() {
        gKitConfig = configurationService.loadConfiguration(GKitConfig.class,
                new File(getDataFolder(), "config.json"));
    }

    public boolean hasLore(ItemStack itemStack) {
        return (itemStack != null && itemStack.getItemMeta() != null
                && itemStack.getItemMeta().getLore() != null
                && !itemStack.getItemMeta().getLore().isEmpty());
    }

    public static GKits get() {
        return JavaPlugin.getPlugin(GKits.class);
    }

}

