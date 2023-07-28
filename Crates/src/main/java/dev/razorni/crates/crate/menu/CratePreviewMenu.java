package dev.razorni.crates.crate.menu;

import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CratePreviewMenu extends Menu {

    private final Crate crate;

    @Override
    public String getTitle(Player player) {
        return crate.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        crate.getItems().forEach(crateItem -> buttonMap.put(crateItem.getSlot(), new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder itemBuilder = new ItemBuilder(crateItem.getItemStack().clone());

                if (crateItem.getPercentage() > 0 && crate.isShowPercentage())
                    itemBuilder.addToLore(CC.translate("&6Percentage: &f" + (crateItem.getFakePercentage() > 0
                            ? crateItem.getFakePercentage() : crateItem.getPercentage())));

                return itemBuilder.build();
            }
        }));

        return buttonMap;
    }
}
