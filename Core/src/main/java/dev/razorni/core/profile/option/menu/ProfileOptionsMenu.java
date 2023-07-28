package dev.razorni.core.profile.option.menu;

import dev.razorni.core.profile.option.event.OptionsOpenedEvent;
import dev.razorni.core.profile.option.menu.button.PrivateChatSoundsOptionButton;
import dev.razorni.core.profile.option.menu.button.PublicChatOptionButton;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.profile.option.menu.button.PrivateChatOptionButton;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class ProfileOptionsMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&6&lOptions";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		buttons.put(buttons.size(), new PublicChatOptionButton());
		buttons.put(buttons.size(), new PrivateChatOptionButton());
		buttons.put(buttons.size(), new PrivateChatSoundsOptionButton());

		OptionsOpenedEvent event = new OptionsOpenedEvent(player);
		event.call();

		if (!event.getButtons().isEmpty()) {
			for (ProfileOptionButton button : event.getButtons()) {
				buttons.put(buttons.size(), button);
			}
		}

		return buttons;
	}

	@Override
	public int size(Player player) {
		return size(getButtons(player));
	}

}
