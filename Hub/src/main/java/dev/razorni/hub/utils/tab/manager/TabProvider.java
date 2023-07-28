package dev.razorni.hub.utils.tab.manager;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */


public interface TabProvider {

    Set<TabLayout> getProvider(Player player);

    List<String> getFooter(Player player);

    List<String> getHeader(Player player);

}
