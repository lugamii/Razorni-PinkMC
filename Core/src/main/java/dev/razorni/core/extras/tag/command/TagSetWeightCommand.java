package dev.razorni.core.extras.tag.command;

import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.util.CC;
import dev.razorni.core.Core;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandSender;

public class TagSetWeightCommand {

    @Command(names = {"tag setweight", "prefix setweight", "prefix setw"}, permission = "gravity.command.prefix.admin", async = true)
    public static void setWeight(CommandSender sender, @Param(name = "tag") Tag tag, @Param(name = "weight") int weight) {
        sender.sendMessage(CC.GREEN + "Set the weight of " + tag.getName() + " to " + tag.getWeight() + '.');
        tag.setWeight(weight);

    }

}
