package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.util.CC;
import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

public class TagCreateCommand {

    @Command(names = {"tag create", "prefix create", "prefix add"}, permission = "gravity.command.tag.admin", async = true)
    public static void createTag(CommandSender sender, @Param(name = "name") String name, @Param(name = "category") String category) {
        if (category.contains("custom")) {

            if (Core.getInstance().getTagHandler().customgetTagByName(name) != null) {
                sender.sendMessage(CC.RED + "A tag with that name already exists.");
                return;
            }
            Tag tag = new Tag(name);
            Core.getInstance().getTagHandler().customsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Created a new tag with the name " + name + CC.GREEN + " in " + category + " category.");
        } else if (category.contains("symbol")) {

            if (Core.getInstance().getTagHandler().symbolgetTagByName(name) != null) {
                sender.sendMessage(CC.RED + "A tag with that name already exists.");
                return;
            }
            Tag tag = new Tag(name);
            Core.getInstance().getTagHandler().symbolsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Created a new tag with the name " + name + CC.GREEN + " in " + category + " category.");
        } else if (category.contains("text")) {

            if (Core.getInstance().getTagHandler().textgetTagByName(name) != null) {
                sender.sendMessage(CC.RED + "A tag with that name already exists.");
                return;
            }
            Tag tag = new Tag(name);
            Core.getInstance().getTagHandler().textsaveTag(tag);

            sender.sendMessage(CC.GREEN + "Created a new tag with the name " + name + CC.GREEN + " in " + category + " category.");
        } else if (category.contains("country")) {

            if (Core.getInstance().getTagHandler().countrygetTagByName(name) != null) {
                sender.sendMessage(CC.RED + "A tag with that name already exists.");
                return;
            }
            Tag tag = new Tag(name);
            Core.getInstance().getTagHandler().countrysaveTag(tag);

            sender.sendMessage(CC.GREEN + "Created a new tag with the name " + name + CC.GREEN + " in " + category + " category.");
        }
    }

}
