package dev.razorni.hub.utils.tab.nms;

import dev.razorni.hub.utils.tab.manager.PlayerTablist;
import dev.razorni.hub.utils.tab.manager.TabColumn;
import dev.razorni.hub.utils.tab.manager.TabEntry;
import dev.razorni.hub.utils.tab.skin.Skin;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public interface TabNMS {

    TabEntry createEntry(
            PlayerTablist playerTablist, String string, TabColumn column, Integer slot, Integer rawSlot
    );

    void updateFakeName(
            PlayerTablist playerTablist, TabEntry tabEntry, String text
    );

    void updateLatency(
            PlayerTablist playerTablist, TabEntry tabEntry, Integer latency
    );

    void updateSkin(
            PlayerTablist playerTablist, TabEntry tabEntry, Skin skin
    );

    void updateHeaderAndFooter(
            Player player, List<String> header, List<String> footer
    );

    Skin getSkin(
            Player player);

}