package dev.razorni.hub.utils.tab.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */

public class LegacyClient {



    public static ArrayList<String> ENTRY = getTabEntrys();
    public static ArrayList<String> NAMES = getTeamNames();

    private static ArrayList<String> getTabEntrys() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String entry = ChatColor.values()[i].toString();
            list.add(ChatColor.RED + entry);
            list.add(ChatColor.GREEN + entry);
            list.add(ChatColor.DARK_RED + entry);
            list.add(ChatColor.DARK_GREEN + entry);
            list.add(ChatColor.BLUE + entry);
            list.add(ChatColor.DARK_BLUE + entry);
        }
        return list;
    }

    private static ArrayList<String> getTeamNames() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 80; ++i) {
            String s = ((i < 10) ? "\\u00010" : "\\u0001") + i;
            list.add(s);
        }
        return list;

    }

}
