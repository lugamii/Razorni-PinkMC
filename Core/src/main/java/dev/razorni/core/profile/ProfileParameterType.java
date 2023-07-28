package dev.razorni.core.profile;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.razorni.core.util.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ProfileParameterType implements ParameterType<Profile> {

    @Override
    public Profile transform(CommandSender sender, String source) {
        for (Profile profile : Profile.getProfiles().values()) {
            if (profile != null && profile.isLoaded()) {
                if (profile.getUsername().equalsIgnoreCase(source)) {
                    return profile;
                }
            }
        }

        sender.sendMessage(ChatColor.RED + "No player with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Profile profile : Profile.getProfiles().values()) {
            if (profile.getPlayer() != null) {
                if (StringUtils.startsWithIgnoreCase(profile.getUsername(), source)) {
                    completions.add(profile.getUsername());
                }
            }
        }

        return completions;
    }

}