package dev.razorni.core.extras.rank;

import dev.razorni.core.util.command.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public final class RankParameterType implements ParameterType<Rank> {
	@Override
	public Rank transform(CommandSender sender, String source) {
		Rank rank = Rank.getRankByDisplayName(source);
		if (rank == null) {
			sender.sendMessage(ChatColor.RED + source + " does not exist.");
			return null;
		}
		return rank;
	}

	@Override
	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		ArrayList<String> completions = new ArrayList<>();
		for (Rank rank : Rank.getRanks().values()) {
			completions.add(rank.getDisplayName());
		}
		return completions;
	}
}