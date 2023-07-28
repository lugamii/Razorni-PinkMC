package dev.razorni.hcfactions.utils;

import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class Logger {
    public static final String LINE_CONSOLE;
    private static final SimpleDateFormat format;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.global;

    static {
        format = new SimpleDateFormat("dd MMMM hh:mm z");
        LINE_CONSOLE = CC.t("&7&m--------------------");
    }

    public Logger() {
    }

    public static void error(String... input) {
        Arrays.stream(input).forEach(logger::warning);
    }

    public static void print(String... input) {
        for (String s : input) {
            Bukkit.getServer().getConsoleSender().sendMessage(CC.t(s));
        }
    }

    public static void info(String... input) {
        Arrays.stream(input).forEach(logger::info);
    }

    private static String convert(String input) {
        return input.equalsIgnoreCase("enabled") ? "Loaded" : "Saved";
    }

    public static void state(String input, int managers, int teams, int users) {
    }
}
