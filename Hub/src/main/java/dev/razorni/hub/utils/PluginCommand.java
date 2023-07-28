package dev.razorni.hub.utils;

import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.shits.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Arrays;


public class PluginCommand {

    public PluginCommand(String name, Class<? extends Command> clazz) {
        try {
            Field field = Hub.getInstance().getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Hub.getInstance().getServer());
            Command command = clazz.newInstance();
            org.bukkit.command.Command cmd = new org.bukkit.command.Command(name) {
                @Override
                public boolean execute(CommandSender commandSender, String s, String[] strings) {
                    return command.execute(commandSender, s, strings);
                }
            };
            commandMap.register(Hub.getInstance().getDescription().getName(), cmd);
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public PluginCommand(String name, Class<? extends Command> clazz, String... aliases) {
        try {
            Field field = Hub.getInstance().getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Hub.getInstance().getServer());
            Command command = clazz.newInstance();
            org.bukkit.command.Command cmd = new org.bukkit.command.Command(name) {
                @Override
                public boolean execute(CommandSender commandSender, String s, String[] strings) {
                    return command.execute(commandSender, s, strings);
                }
            }.setAliases(Arrays.asList(aliases));
            commandMap.register(Hub.getInstance().getDescription().getName(), cmd);
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
