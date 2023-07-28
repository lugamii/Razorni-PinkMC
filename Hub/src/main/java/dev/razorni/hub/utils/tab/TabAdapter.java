package dev.razorni.hub.utils.tab;

import dev.razorni.hub.utils.tab.manager.PlayerTablist;
import dev.razorni.hub.utils.tab.manager.TabListener;
import dev.razorni.hub.utils.tab.manager.TabProvider;
import dev.razorni.hub.utils.tab.nms.TabNMS;
import dev.razorni.hub.utils.tab.nms.v1_7_R4.Tab_v1_7_R4;
import dev.razorni.hub.utils.tab.nms.v1_8_R3.Tab_v1_8_R3;
import dev.razorni.hub.utils.tab.thread.TabThread;
import dev.razorni.hub.utils.tab.versions.PlayerVersionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */

@Getter
public class TabAdapter {


    @Getter
    private static TabAdapter instance;

    private final JavaPlugin plugin;
    private final TabProvider provider;
    private final Map<UUID, PlayerTablist> playerTablist;
    private TabThread tabThread;
    private TabNMS tabNMS;
    private PlayerVersionManager playerVersionManager;

    public TabAdapter(JavaPlugin plugin, TabProvider provider) {
        if (plugin == null) {
            throw new RuntimeException("NULL!!!!1");
        }

        instance = this;
        this.plugin = plugin;
        this.provider = provider;
        this.playerTablist = new ConcurrentHashMap<>();
        this.playerVersionManager = new PlayerVersionManager();

        this.registerNMS();
        this.setup();
    }

    private void registerNMS() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        if (serverVersion.equalsIgnoreCase("v1_7_R4")) {
            this.tabNMS = new Tab_v1_7_R4();
            System.out.println("[Hub] Registered NMS with v1.7R4 TabAdapter");
        }else
        if (serverVersion.equalsIgnoreCase("v1_8_R3")) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {

                this.tabNMS = new Tab_v1_8_R3();
                System.out.println("[Hub] Registered NMS with 1.8R3 TabAdapter (ProtocolLib)");
            }else{
                System.out.println("[Hub] Unable to register 1.8R3 TabAdapter! Please add ProtocolLib ");
            }
        }
    }

    private void setup() {
        this.plugin.getServer().getPluginManager().registerEvents(new TabListener(), this.plugin);

        if (this.tabThread != null) {
            this.tabThread.stop();
            this.tabThread = null;
        }

        this.tabThread = new TabThread(this);
    }

    public void disable() {
        if (this.tabThread != null) {
            this.tabThread.stop();
            this.tabThread = null;
        }
        for (UUID uuid : getPlayerTablist().keySet()) {
            getPlayerTablist().remove(uuid);
        }
    }


}
