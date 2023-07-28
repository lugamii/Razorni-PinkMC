package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.menu.CategorySelectorMenu;
import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;

public class TagSelectorCommand {

    @Command(names = {"prefix", "tags", "tag", "prefixes"}, permission = "", async = true)
    public static void prefix(final Player player) {
        new CategorySelectorMenu().openMenu(player);
    }
}