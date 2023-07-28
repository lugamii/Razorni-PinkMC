package dev.razorni.hcfactions.extras.framework.menu;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.extras.framework.menu.listener.MenuListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {
    private final Map<UUID, Menu> menus;

    public MenuManager(HCF plugin) {
        super(plugin);
        this.menus = new HashMap<>();
        new MenuListener(this);
    }

}
