package dev.razorni.core.chat;

import dev.razorni.core.Core;
import dev.razorni.core.chat.filter.ChatFilter;
import dev.razorni.core.profile.Profile;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class Chat {

	public Core core;

	public Chat(Core core) {
		this.core = core;
	}

	@Getter @Setter private int delayTime = 3;
	@Getter private boolean publicChatMuted = false;
	@Getter private boolean publicChatDelayed = false;
	@Getter private final List<ChatFilter> filters = new ArrayList<>();
	@Getter private List<String> filteredPhrases = new ArrayList<>();
	@Getter private List<String> linkWhitelist = new ArrayList<>();

	public void setPublicChatMuted(boolean muted) {
		if (this.publicChatMuted == muted) {
			return;
		}

		publicChatMuted = !publicChatMuted;
	}

	public ChatAttempt attemptChatMessage(Player player, String message) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());

		if (profile.getActiveMute() != null) {
			return new ChatAttempt(ChatAttempt.Response.PLAYER_MUTED, profile.getActiveMute());
		}

		if (publicChatMuted && !player.hasPermission("core.mod")) {
			return new ChatAttempt(ChatAttempt.Response.CHAT_MUTED);
		}

		String msg = message.toLowerCase()
		                    .replace("3", "e")
		                    .replace("1", "i")
		                    .replace("!", "i")
		                    .replace("@", "a")
		                    .replace("7", "t")
		                    .replace("0", "o")
		                    .replace("5", "s")
		                    .replace("8", "b")
		                    .replaceAll("\\p{Punct}|\\d", "").trim();

		String[] words = msg.trim().split(" ");

		for (ChatFilter chatFilter : this.filters) {
			if (chatFilter.isFiltered(msg, words)) {
				return new ChatAttempt(ChatAttempt.Response.MESSAGE_FILTERED);
			}
		}

		return new ChatAttempt(ChatAttempt.Response.ALLOWED);
	}

}
