package org.bukkit.command.defaults;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class VersionCommand extends BukkitCommand {
    public VersionCommand() {
        super("version");

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        TextComponent message = new TextComponent(
                "This server is running " + ChatColor.RED + "InvictusSpigot "
                + ChatColor.WHITE + "(" + Bukkit.getBukkitVersion() + ") "
                + "by " + ChatColor.RED + "Vortex Development " + ChatColor.DARK_GRAY + "(Click)");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/j39HfaP7zN"));
        sender.sendMessage(message);
        return true;
    }

}