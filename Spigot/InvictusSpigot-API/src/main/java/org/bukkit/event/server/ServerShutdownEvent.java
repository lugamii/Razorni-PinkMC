package org.bukkit.event.server;

import org.bukkit.Server;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

public final class ServerShutdownEvent extends ServerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Server server;

    @ConstructorProperties({"server"})
    public ServerShutdownEvent(Server server) {
        this.server = server;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Server getServer() {
        return this.server;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
