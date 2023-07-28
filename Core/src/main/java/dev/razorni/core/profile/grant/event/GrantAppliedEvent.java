package dev.razorni.core.profile.grant.event;

import dev.razorni.core.util.BaseEvent;
import dev.razorni.core.profile.grant.Grant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class GrantAppliedEvent extends BaseEvent {

	private Player player;
	private Grant grant;

}
