package dev.razorni.hcfactions.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CC {
    public static String LINE;

    static {
        CC.LINE = t("&7&m-------------------");
    }

    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> translate(List<String> s) {
        return s.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static List<String> list(List<String> s) {
        List<String> strings = new ArrayList<>();
        s.forEach(str -> strings.add(ChatColor.translateAlternateColorCodes('&', str)));
        return strings;
    }

    public static String Color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static OfflinePlayer getPlayer(String offline) {
        Player player = Bukkit.getPlayer(offline);
        if (player != null) {
            return player;
        }
        return Bukkit.getOfflinePlayer(offline);
    }

    public static List<String> t(List<String> translate) {
        return translate.stream().map(CC::t).collect(Collectors.toList());
    }

    public static String t(String translate) {
        return ChatColor.translateAlternateColorCodes('&', translate);
    }
}
