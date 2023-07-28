package eu.vortexdev.invictusspigot.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class BukkitUtil {

    public static String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------";

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static ArrayList<String> translate(String... strings) {
        ArrayList<String> list = Lists.newArrayList();
        for (String string : strings)
            list.add(ChatColor.translateAlternateColorCodes('&', string));
        return list;
    }

    public static ArrayList<String> translate(List<String> strings) {
        ArrayList<String> list = Lists.newArrayList();
        for (String string : strings)
            list.add(ChatColor.translateAlternateColorCodes('&', string));
        return list;
    }

}
