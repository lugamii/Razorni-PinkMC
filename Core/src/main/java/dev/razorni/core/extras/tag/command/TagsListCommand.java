package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.util.CC;
import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

public class TagsListCommand {

    @Command(names = {"tag list", "tags list", "prefix list", "prefixes"}, permission = "gravity.command.tag.admin")
    public static void listTags(CommandSender sender, @Param(name = "category") String category) {
        sender.sendMessage(CC.CHAT_BAR);
        if (category.contains("custom")) {
            sender.sendMessage(CC.YELLOW + "Listing all custom prefixes");
            for (Tag tag : Core.getInstance().getTagHandler().getCustomtags()) {
                sender.sendMessage(CC.RED + tag.getName() + CC.YELLOW + " " + tag.getPrefixInfo() + CC.GRAY + " (Displays as: " + (CC.translate(tag.getPrefix())) + CC.GRAY + ")");
            }
        } else if (category.contains("symbol")) {
            sender.sendMessage(CC.YELLOW + "Listing all symbol prefixes");
            for (Tag tag : Core.getInstance().getTagHandler().getSymboltags()) {
                sender.sendMessage(CC.RED + tag.getName() + CC.YELLOW + " " + tag.getPrefixInfo() + CC.GRAY + " (Displays as: " + (CC.translate(tag.getPrefix())) + CC.GRAY + ")");
            }
        } else if (category.contains("text")) {
            sender.sendMessage(CC.YELLOW + "Listing all text prefixes");
            for (Tag tag : Core.getInstance().getTagHandler().getTexttags()) {
                sender.sendMessage(CC.RED + tag.getName() + CC.YELLOW + " " + tag.getPrefixInfo() + CC.GRAY + " (Displays as: " + (CC.translate(tag.getPrefix())) + CC.GRAY + ")");
            }
        } else if (category.contains("country")) {
            sender.sendMessage(CC.YELLOW + "Listing all country prefixes");
            for (Tag tag : Core.getInstance().getTagHandler().getCountrytags()) {
                sender.sendMessage(CC.RED + tag.getName() + CC.YELLOW + " " + tag.getPrefixInfo() + CC.GRAY + " (Displays as: " + (CC.translate(tag.getPrefix())) + CC.GRAY + ")");
            }
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

}
