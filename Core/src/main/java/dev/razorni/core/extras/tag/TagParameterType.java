package dev.razorni.core.extras.tag;

import dev.razorni.core.Core;
import dev.razorni.core.util.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class TagParameterType implements ParameterType<Tag> {

    @Override
    public Tag transform(CommandSender sender, String source) {
        for (Tag tag : Core.getInstance().getTagHandler().getCustomtags()) {
            if (!sender.hasPermission("gravity.command.tag.admin")) {
                continue;
            }

            if (tag.getName().equalsIgnoreCase(source)) {
                return tag;
            }
        }

        for (Tag tag : Core.getInstance().getTagHandler().getCountrytags()) {
            if (!sender.hasPermission("gravity.command.tag.admin")) {
                continue;
            }

            if (tag.getName().equalsIgnoreCase(source)) {
                return tag;
            }
        }

        for (Tag tag : Core.getInstance().getTagHandler().getTexttags()) {
            if (!sender.hasPermission("gravity.command.tag.admin")) {
                continue;
            }

            if (tag.getName().equalsIgnoreCase(source)) {
                return tag;
            }
        }

        for (Tag tag : Core.getInstance().getTagHandler().getSymboltags()) {
            if (!sender.hasPermission("gravity.command.tag.admin")) {
                continue;
            }

            if (tag.getName().equalsIgnoreCase(source)) {
                return tag;
            }
        }

        sender.sendMessage(ChatColor.RED + "No tag with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Tag tag : Core.getInstance().getTagHandler().getCustomtags()) {
            if (!player.hasPermission("gravity.command.tag.admin")) {
                continue;
            }

            if (StringUtils.startsWithIgnoreCase(tag.getName(), source)) {
                completions.add(tag.getName());
            }
        }

        return completions;
    }

}