package dev.razorni.hcfactions.utils.tablist;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.providers.rTablist;
import dev.razorni.hcfactions.utils.tablist.extra.TablistSkin;
import dev.razorni.hcfactions.utils.tablist.listener.TablistListener;
import dev.razorni.hcfactions.utils.tablist.packet.TablistPacket;
import dev.razorni.hcfactions.utils.tablist.thread.TablistThread;
import dev.razorni.hcfactions.utils.Logger;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TablistManager extends Manager {
    private final Map<UUID, Tablist> tablists;
    private final TablistAdapter adapter;
    private final Map<String, TablistSkin> skins;

    public TablistManager(HCF plugin) {
        super(plugin);
        this.tablists = new ConcurrentHashMap<>();
        this.skins = new ConcurrentHashMap<>();
        this.adapter = new rTablist(this);
        this.load();
        new TablistListener(this);
        new TablistThread(this);
    }

    private void load() {
        if (Bukkit.getServer().getMaxPlayers() <= 80) {
            Logger.print(Logger.LINE_CONSOLE, "&cPlease increase slots to 80 so tablist can work.", Logger.LINE_CONSOLE);
        }
        for (String s : this.getTablistConfig().getConfigurationSection("SKINS").getKeys(false)) {
            String path = "SKINS." + s + ".";
            this.skins.put(s, new TablistSkin(this.getTablistConfig().getString(path + "VALUE"), this.getTablistConfig().getString(path + "SIGNATURE")));
        }
    }

    @SneakyThrows
    public TablistPacket createPacket(Player player) {
        String skin = "dev.razorni.hcfactions.utils.tablist.packet.type.TablistPacketV" + Utils.getNMSVer();
        return (TablistPacket) Class.forName(skin).getConstructor(TablistManager.class, Player.class).newInstance(this, player);
    }
}