package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandSender;

public class TagDeleteCommand {

    @Command(names = {"tag delete", "prefix delete", "prefix remove"}, permission = "gravity.command.tag.admin", async = true)
    public static void deleteTag(CommandSender sender, @Param(name = "tag") Tag tag,  @Param(name = "category") String category) {
        if (category.contains("custom")) {
            Core.getInstance().getTagHandler().customremoveTag(tag);

            sender.sendMessage(CC.GREEN + "Removed the tag '" + tag.getName() + "' from " + category + " category.");
        } else if (category.contains("symbol")) {
            Core.getInstance().getTagHandler().symbolremoveTag(tag);

            sender.sendMessage(CC.GREEN + "Removed the tag '" + tag.getName() + "' from " + category + " category.");
        } else if (category.contains("text")) {
            Core.getInstance().getTagHandler().textremoveTag(tag);

            sender.sendMessage(CC.GREEN + "Removed the tag '" + tag.getName() + "' from " + category + " category.");
        } else if (category.contains("country")) {
            Core.getInstance().getTagHandler().countryremoveTag(tag);

            sender.sendMessage(CC.GREEN + "Removed the tag '" + tag.getName() + "' from " + category + " category.");
        }
    }
}
