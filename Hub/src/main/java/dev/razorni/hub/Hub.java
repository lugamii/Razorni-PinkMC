package dev.razorni.hub;

import dev.razorni.core.Core;
import dev.razorni.hub.commands.JoinQueueCommand;
import dev.razorni.hub.commands.LeaveQueueCommand;
import dev.razorni.hub.commands.ToggleQueueCommand;
import dev.razorni.hub.framework.Manager;
import dev.razorni.hub.framework.menu.MenuManager;
import dev.razorni.hub.listeners.DoubleJumpListener;
import dev.razorni.hub.listeners.LunarClientListener;
import dev.razorni.hub.listeners.ServerListener;
import dev.razorni.hub.providers.rHolograms;
import dev.razorni.hub.providers.rTab;
import dev.razorni.hub.providers.rTags;
import dev.razorni.hub.queue.QueueHandler;
import dev.razorni.hub.queue.QueueManager;
import dev.razorni.hub.utils.PluginCommand;
import dev.razorni.hub.utils.board.BoardManager;
import dev.razorni.hub.utils.configs.ConfigYML;
import dev.razorni.hub.utils.hologramapi.HologramsManager;
import dev.razorni.hub.utils.shits.BungeeUtils;
import dev.razorni.hub.utils.shits.CC;
import dev.razorni.hub.utils.shits.FileConfig;
import dev.razorni.hub.utils.tab.TabAdapter;
import dev.razorni.hub.versions.VersionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class Hub extends JavaPlugin {

    @Getter
    private static Hub instance;

    private PacketPlayOutChat packet;

    private int actionbar = 0;
    private long lastMillisActionbar = System.currentTimeMillis();

    private List<Manager> managers;
    private VersionManager versionManager;
    private FileConfig settingsConfig;
    private MenuManager menuManager;
    private QueueHandler queueHandler;
    private QueueManager queueManager;
    private List<ConfigYML> configs;

    @Override
    public void onEnable() {
        instance = this;
        this.settingsConfig = new FileConfig(this, "settings.yml");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeUtils());
        this.managers = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.versionManager = new VersionManager(this);
        this.menuManager = new MenuManager(this);
        queueManager = new QueueManager();
        queueHandler = new QueueHandler();
        new BoardManager(this);
        TabAdapter tabAdapter = new TabAdapter(Hub.getInstance(), new rTab());
        new LunarClientListener();
        new rTags();
        new rHolograms().register();
        new HologramsManager(this);
        if (Hub.getInstance().getSettingsConfig().getConfig().getBoolean("TITLES.ACTION-BAR.ENABLED")) {
            updateActionBar();
        }
        registerCommands();
        registerListeners();
        // Plugin startup logic

    }

    private void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DoubleJumpListener(), this);
    }

    private void registerCommands() {
        new PluginCommand("leavequeue", LeaveQueueCommand.class);
        new PluginCommand("togglequeue", ToggleQueueCommand.class,"pausequeue");
        new PluginCommand("joinqueue", JoinQueueCommand.class, "play");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void updateActionBar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hub.getInstance(), new Runnable() {

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    sendActionBar(p);
                }
            }
        }, 0L, 20L);
    }

    private void sendActionBar(Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ActionBarList() + "\"}"), (byte) 2);
        this.packet = packet;
        if (!Core.getInstance().getStaffManager().isStaffMode(player)) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private String ActionBarList() {
        List<String> footers = CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TITLES.ACTION-BAR.CHANGES"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(3699);

        if (lastMillisActionbar + interval <= time) {
            if (actionbar != footers.size() - 1) {
                actionbar++;
            } else {
                actionbar = 0;
            }
            lastMillisActionbar = time;
        }
        return footers.get(actionbar);
    }

}
