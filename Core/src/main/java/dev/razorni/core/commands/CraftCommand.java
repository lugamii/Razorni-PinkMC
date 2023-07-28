package dev.razorni.core.commands;

import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;

public class CraftCommand {

    @Command(names = {"craft", "wb", "workbench"}, permission = "gravity.command.craft")
    public static void craft(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

}

