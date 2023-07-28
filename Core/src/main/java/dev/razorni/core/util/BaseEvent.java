package dev.razorni.core.util;

import dev.razorni.core.Core;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public void call() {
		Core.getInstance().getServer().getPluginManager().callEvent(this);
	}

}
