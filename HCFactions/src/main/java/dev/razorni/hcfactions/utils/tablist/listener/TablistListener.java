package dev.razorni.hcfactions.utils.tablist.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.tablist.Tablist;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TablistListener extends Module<TablistManager> {

    public TablistListener(TablistManager tablistManager) {
        super(tablistManager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getTablists().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Tablist tablist = new Tablist(this.getManager(), player);
        Tasks.executeLater(this.getManager(), 10, () -> this.getManager().getTablists().put(player.getUniqueId(), tablist));
    }
}
