package dev.razorni.core.profile.option.event;

import dev.razorni.core.util.BaseEvent;
import dev.razorni.core.profile.option.menu.ProfileOptionButton;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class OptionsOpenedEvent extends BaseEvent {

	private final Player player;
	private List<ProfileOptionButton> buttons = new ArrayList<>();

}
