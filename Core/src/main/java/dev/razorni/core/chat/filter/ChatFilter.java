package dev.razorni.core.chat.filter;

import dev.razorni.core.Core;
import org.bukkit.entity.Player;

public abstract class ChatFilter {

	private String command;
	public Core core;

	public ChatFilter(Core core, String command) {
		this.core = core;

		this.command = command;
	}

	public abstract boolean isFiltered(String message, String[] words);

	public void punish(Player player) {
		if (command != null) {
			core.getServer().dispatchCommand(core.getServer().getConsoleSender(), command
					.replace("{player}", player.getName())
					.replace("{player-uuid}", player.getUniqueId().toString()));
		}
	}
}
