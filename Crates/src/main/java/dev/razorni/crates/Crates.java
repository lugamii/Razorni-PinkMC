package dev.razorni.crates;

import dev.razorni.crates.command.CrateCommands;
import dev.razorni.crates.command.LootboxCommands;
import dev.razorni.crates.command.parameter.CrateParameter;
import dev.razorni.crates.command.parameter.LootBoxParameter;
import dev.razorni.crates.crate.Crate;
import dev.razorni.crates.crate.CrateManager;
import dev.razorni.crates.crate.listener.CrateListener;
import dev.razorni.crates.lootbox.LootBox;
import dev.razorni.crates.lootbox.LootBoxManager;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.configuration.ConfigurationService;
import cc.invictusgames.ilib.configuration.JsonConfigurationService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.stream.Stream;

@Getter
public class Crates extends JavaPlugin {

    public static final DecimalFormat REWARD_FORMAT = new DecimalFormat("####.##");

    private ConfigurationService configurationService;
    private CrateManager crateManager;
    private LootBoxManager lootBoxManager;

    @Override
    public void onEnable() {
        configurationService = new JsonConfigurationService();

        if (!getDataFolder().exists())
            if (getDataFolder().mkdir())
                getLogger().info("Plugin directory didn't exist, so we created it for you!");

        CommandService.registerParameter(Crate.class, new CrateParameter(this));
        CommandService.registerParameter(LootBox.class, new LootBoxParameter(this));
        CommandService.register(this,
                new CrateCommands(this, crateManager = new CrateManager(this)),
                new LootboxCommands(this, lootBoxManager = new LootBoxManager(this))
        );

        Stream.of(new CrateListener(this))
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        crateManager.saveCrates(false);
        lootBoxManager.saveLootBoxes(false);

        for (Crate value : crateManager.getCrateMap().values()) {
            value.deleteHologram();
        }
    }

    public static Crates get() {
        return JavaPlugin.getPlugin(Crates.class);
    }
}
