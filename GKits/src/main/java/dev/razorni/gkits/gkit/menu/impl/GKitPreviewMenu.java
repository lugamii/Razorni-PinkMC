package dev.razorni.gkits.gkit.menu.impl;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.gkits.GKits;
import dev.razorni.gkits.gkit.GKit;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.menu.buttons.BackButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class GKitPreviewMenu extends Menu {

    private final GKit gKit;
    private final GKits plugin;

    @Override
    public String getTitle(Player player) {
        return CC.YELLOW + "Preview: " + CC.YELLOW + gKit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        for (ItemStack itemStack : gKit.getContents())
            buttonMap.put(buttonMap.size(), Button.createPlaceholder(itemStack));

        AtomicInteger atomicInteger = new AtomicInteger(41);

        for (ItemStack itemStack : gKit.getArmor())
            buttonMap.put(atomicInteger.getAndIncrement(), Button.createPlaceholder(itemStack));

        buttonMap.put(36,
                new BackButton(new GKitMenu(plugin, plugin.getProfileManager().getProfile(player.getUniqueId()))));

        return buttonMap;
    }
}
