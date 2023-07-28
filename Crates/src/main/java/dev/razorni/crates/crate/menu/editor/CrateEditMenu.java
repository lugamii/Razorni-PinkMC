package dev.razorni.crates.crate.menu.editor;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import dev.razorni.crates.crate.menu.editor.buttons.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class CrateEditMenu extends Menu {

    private final Crate crate;
    private final Crates plugin;

    @Override
    public String getTitle(Player player) {
        return "Editing " + crate.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(buttonMap.size(), new CrateEditItemsButton(crate, plugin));
        buttonMap.put(buttonMap.size(), new CrateBroadcastButton(crate));
        buttonMap.put(buttonMap.size(), new CrateRewardAmountButton(crate));
        buttonMap.put(buttonMap.size(), new CrateSetDisplayNameButton(crate));
        buttonMap.put(buttonMap.size(), new CrateSetKeyItemButton(crate));
        buttonMap.put(buttonMap.size(), new CrateMetaDataButton(crate));
        buttonMap.put(buttonMap.size(), new CrateShowPercentageButton(crate));
        buttonMap.put(buttonMap.size(), new CrateLocationItemButton(crate));
        buttonMap.put(buttonMap.size(), new CrateHologramButton(crate));
        return buttonMap;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }
}
