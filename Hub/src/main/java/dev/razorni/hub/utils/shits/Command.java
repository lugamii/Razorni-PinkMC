package dev.razorni.hub.utils.shits;

import org.bukkit.command.CommandSender;


public abstract class Command {

    public abstract boolean execute(CommandSender sender, String label, String[] args);
}
