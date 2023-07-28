package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.util.CC;
import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

public class TagSetPrefixCommand {

    @Command(names = {"tag setprefix", "prefix setprefix"}, permission = "gravity.command.tag.admin", async = true)
    public static void setTag(CommandSender sender, @Param(name = "tag") Tag tag, @Param(name = "category") String category, @Param(name = "newPrefix", wildcard = true) String newPrefix) {
        if (category.contains("custom")) {
            tag.setPrefix(CC.translate(newPrefix));
            Core.getInstance().getTagHandler().customsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Set the prefix of " + tag.getName() + " to " + tag.getPrefix() + " in " + category + " category.");

        } else if (category.contains("text")) {
            tag.setPrefix(CC.translate(newPrefix));
            Core.getInstance().getTagHandler().textsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Set the prefix of " + tag.getName() + " to " + tag.getPrefix() + " in " + category + " category.");

        } else if (category.contains("symbol")) {
            tag.setPrefix(CC.translate(newPrefix));
            Core.getInstance().getTagHandler().symbolsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Set the prefix of " + tag.getName() + " to " + tag.getPrefix() + " in " + category + " category.");

        } else if (category.contains("country")) {
            tag.setPrefix(CC.translate(newPrefix));
            Core.getInstance().getTagHandler().countrysaveTag(tag);

            sender.sendMessage(CC.GREEN + "Set the prefix of " + tag.getName() + " to " + tag.getPrefix() + " in " + category + " category.");

        }
    }

}
