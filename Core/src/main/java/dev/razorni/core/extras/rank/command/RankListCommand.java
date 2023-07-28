package dev.razorni.core.extras.rank.command;

import dev.razorni.core.extras.rank.comparator.RankComparator;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.extras.rank.Rank;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class RankListCommand {

    @Command(names = "rank list", permission = "gravity.command.rank", async = true, description = "Show all existing ranks along with their weight.")

    public static void list(CommandSender sender) {
        List<Rank> ranks = Rank.getRanks().values().stream().sorted(new RankComparator()).collect(Collectors.toList());
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.YELLOW + "Listing all ranks...");
        for (Rank rank : ranks) {
            sender.sendMessage(rank.formattedName() + (rank.isDefaultRank() ? " (Default)" : "") +
                    " (Weight: " + rank.getWeight() + ")");
        }
        sender.sendMessage(CC.CHAT_BAR);
    }
}