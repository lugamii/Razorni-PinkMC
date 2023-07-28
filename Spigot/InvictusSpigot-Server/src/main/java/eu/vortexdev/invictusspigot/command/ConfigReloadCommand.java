package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConfigReloadCommand extends Command {

    public ConfigReloadCommand() {
        super("configreload");
        setDescription("Reload config files without restarting server");
        setPermission("invictusspigot.configreload");
    }

    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (testPermission(sender)) {
            Bukkit.reloadConfigs();
            sender.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "Reloaded all spigot configs");
        }
        return false;
    }
}
