package dev.razorni.crates.lootbox.menu.editor;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import dev.razorni.crates.lootbox.menu.editor.buttons.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class LootBoxEditMenu extends Menu {

    private final LootBox lootBox;
    private final Crates plugin;

    @Override
    public String getTitle(Player player) {
        return "Editing " + lootBox.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(buttonMap.size(), new LootBoxEditItemsButton(lootBox, plugin));
        buttonMap.put(buttonMap.size(), new LootBoxEditFinalItemsButton(lootBox, plugin));
        buttonMap.put(buttonMap.size(), new LootBoxSetDisplayNameButton(lootBox));
        buttonMap.put(buttonMap.size(), new LootBoxMetaDataButton(lootBox));
        buttonMap.put(buttonMap.size(), new LootBoxFinalMetaDataButton(lootBox));
        return buttonMap;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }
}
