package dev.razorni.core.commands;


import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.extras.rank.comparator.RankComparator;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.profile.Profile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand {

    @Command(names = {"list", "who"}, permission = "")
    public static void list(CommandSender sender) {
        List<Rank> ranks = Rank.getRanks().values().stream().sorted(new RankComparator()).collect(Collectors.toList());
        List<String> rankStr = ranks.stream().filter(rank -> !rank.getPermissions().contains("hidden")).map(rank -> rank.getColor() + rank.getDisplayName()).collect(Collectors.toList());
        sender.sendMessage(StringUtils.join(rankStr, CC.GRAY + ", "));
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream().sorted(Comparator.comparingInt(player -> Profile.getByUuid(player.getUniqueId()).getActiveRank().getWeight())).collect(Collectors.toList());
        Collections.reverse(onlinePlayers);
        String onlinePlayersOutOfMaximumPlayers = CC.WHITE + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") ";
        if (sender.hasPermission("gravity.staff")) {
            List<String> list = new ArrayList<>();
            for (Player player : onlinePlayers) {
                String vanished = (player.hasMetadata("invisible") ? CC.GRAY + "*" : "") + Profile.getByUuid(player.getUniqueId()).getColoredUsername();
                list.add(vanished);
            }
            sender.sendMessage(onlinePlayersOutOfMaximumPlayers + StringUtils.join(list, CC.GRAY + ", "));
        } else {
            List<String> list = new ArrayList<>();
            for (Player player : onlinePlayers) {
                if (!player.hasMetadata("invisible")) {
                    String coloredUsername = Profile.getByUuid(player.getUniqueId()).getColoredUsername();
                    list.add(coloredUsername);
                }
            }
            sender.sendMessage(onlinePlayersOutOfMaximumPlayers + StringUtils.join(list, CC.GRAY + ", ")); }
    }

}
