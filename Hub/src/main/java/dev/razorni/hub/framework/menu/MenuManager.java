package dev.razorni.hub.framework.menu;

import dev.razorni.hub.Hub;
import dev.razorni.hub.framework.Manager;
import dev.razorni.hub.framework.menu.listener.MenuListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {
    private final Map<UUID, Menu> menus;

    public MenuManager(Hub plugin) {
        super(plugin);
        this.menus = new HashMap<>();
        new MenuListener(this);
    }

}
